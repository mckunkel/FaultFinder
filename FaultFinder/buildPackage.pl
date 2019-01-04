#!/apps/bin/perl -w
use strict;
use warnings;
use feature qw{ say };
use XML::Twig;

#Global Variable
my $scriptDir  = "scripts/JLabCluster/";
my $envFile    = $scriptDir . "environment.cshrc";
my $buildScript  = $scriptDir . "build_DCFaultFinder.cshrc";

#Ok, I dont understand how to use POD:Usage along with other options, so I will hard code the usage here

if ( defined $ARGV[0] && $ARGV[0] eq "--help" ) {
	print STDOUT "Options:\n",
	  "--help				this \n \n",
	  "Usage:\n",
	  "./buildPackage.pl  \n";
}

#Global variables
my $maven_Dir = "$ENV{HOME}/.m2";

my $settings_File = "settings.xml";

#Check if you are building on jlab
if ( `hostname` =~ m/.jlab.org/ ) {
	ProcessJLab();
}

sub ProcessJLab {
	if ( CheckMaven() ) {
		print("Maven directory is ready. \n");
		BuildDCFaultFinder();
	}
	else {
		print STDERR "There is an error, \n
         Maven directory is not ready. \n 
         Probably ~/.m2/settings.xml already exists with incorrect proxy settings. \n
         See hint above. \n";
	}
}

sub CheckMaven {

	#No Maven directory, therefore no settings.xml
	if ( !-d $maven_Dir ) {
		system("mkdir $maven_Dir");
		system("cp $settings_File $maven_Dir");
		return 1;
	}

	#There is a .m2 directory, lets check if settings are there
	else {
		if ( !-e ( $maven_Dir . "/" . $settings_File ) ) {
			system("cp $settings_File $maven_Dir");
			return 1;
		} #There is a .m2 directory and settings.xml, lets check if settings have the correct Jlab settings
		else {
			return CheckSettingsFile();
		}
	}

}

sub CheckSettingsFile {

	my ( $PORT, $HOST, $PROTOCOL, $ACTIVE ) = ( 8082, 'jprox', 'http', 'true' );
	my $twig =
	  'XML::Twig'->new( pretty_print => 'indented', )
	  ->parsefile( $maven_Dir . "/" . $settings_File );

	my $root = $twig->root;
	my ($proxies) = $root->children('proxies');

	if ( !$proxies ) {
		print STDERR "In your settings.xml located in your .m2 folder,
         there appears to be no proxy settings.
         Adding Jlab proxy settings \n";

		my $proxies = $root->insert_new_elt('proxies');
		$proxies->set_inner_xml(
			    "<proxy><active>$ACTIVE</active><protocol>$PROTOCOL</protocol>"
			  . "<host>$HOST</host><port>$PORT</port></proxy>" );
		$root->print;

		my $outDir = $maven_Dir . "/" . $settings_File;
		open( my $fh_out, '>', $outDir )
		  or die "unable to open '$outDir' for writing: $!";
		print {$fh_out} $twig->sprint();
		return 1;

	}
	else {
		my $found;
		for my $proxy ( $proxies->children('proxy') ) {
			my $port     = $proxy->first_child_text('port');
			my $host     = $proxy->first_child_text('host');
			my $protocol = $proxy->first_child_text('protocol');
			my $active   = $proxy->first_child_text('active');

			$found = 1
			  if $port == $PORT
			  && $host eq $HOST
			  && $protocol eq $PROTOCOL
			  && $active eq $ACTIVE;
		}
		if ($found) {
			print STDERR "Correct proxy settings are set properly for Jlab \n";
			return 1;
		}
		else {
			print STDERR "Correct proxy settings missing:\n",
			  "port $PORT\nhost $HOST\nprotocol $PROTOCOL\nactive $ACTIVE\n";
			return 0;

		}
	}

}

sub BuildDCFaultFinder {
  system("cp $buildScript . ");
  system("cp $envFile . ");
	system("./build_DCFaultFinder.cshrc");
	system ("rm build_DCFaultFinder.cshrc");
	system ("rm environment.cshrc");
	
}

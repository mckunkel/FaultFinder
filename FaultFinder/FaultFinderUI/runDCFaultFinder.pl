#!/apps/bin/perl -w
use strict;
use warnings;
use feature qw{ say };
use XML::Twig;

#Global Variable
my $scriptDir  = "scripts/JLabCluster/";
my $envFile    = $scriptDir . "environment.cshrc";
my $runScript  = $scriptDir . "run_DCFaultFinder.cshrc";
my $javaDir    = "target/";
my $javaScript = $javaDir . "DCFaultFinderApp-jar-with-dependencies.jar";

#Ok, I dont understand how to use POD:Usage along with other options,
#so I will hard code the usage here

if ( defined $ARGV[0] && $ARGV[0] eq "--help" ) {
	print STDOUT "Options:\n",
	  "--help				this \n \n",
	  "Usage:\n",
	  "./runDCFaultFinder.pl  \n",
	  "Notes:\n",
"Running this will set your environment according to the CLASTAG set in the default  \n",
	  "setting in the /group/clas12/gemc/environment.csh script",
	  ;
}
if ( CheckFiles() ) {

	system("cp $runScript . ");
	system("cp $envFile . ");
	system("./run_DCFaultFinder.cshrc $javaScript");
	system("rm run_DCFaultFinder.cshrc");
	system("rm environment.cshrc");
}

sub CheckFiles {

	if ( !( -d $scriptDir ) ) {
		print STDERR "Script not running in parent directory \n",
		  "or script directory is missing";
		return 0;
	}
	if ( !( -d $javaDir ) ) {
		print STDERR "Problem: Script not running in parent directory \n",
		  "or target directory is missing. Possible rebuild of package needed.";
		return 0;
	}
	elsif ( !( -e $javaScript ) ) {
		print STDERR "It appears that the package is not built \n", return 0;
	}
	elsif ( !( -e $envFile ) ) {
		print STDERR
		  "It appears that the environment.cshrc is ",
		  "missing from the script directory \n";
		return 0;
	}
	elsif ( !( -e $runScript ) ) {
		print STDERR
		  "It appears that therun_DCFaultFinder.cshrc is ",
		  "missing from the script directory \n";
		return 0;
	}
	else {
		return 1;
	}

}


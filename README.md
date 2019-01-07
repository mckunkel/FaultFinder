# FaultFinder
The CLAS12 Drift Chamber Fault Finder with Artificial Intelligence (AI) Recommendation System.
Same interface as DCFaultFinder
Same user instructions as DCFaultFinder
AI Recommendations happen automatically

To build and use see [DCFaultFinder](https://github.com/mckunkel/DCFaultFinder)
Documentation for AI
The AI runs on a 2-tier redundant custom object detection system.
The first tier is a convolutional neural network model, designated KunkelPetersModel, that detects is a specific fault exists in the data. For instance, a classifier Channel_1 was trained to detect the presence of Channel_1. There are 12 other classifiers for all the possible hardware malfunctions;
All Classifiers: Channel_1, Channel_2, Channel_3, Pin_small, Pin_big, Connector_A, Connector_B, Connector_C, Fuse_A, Fuse_B, Fuse_C, Deadwire, HotWire.
For complete details on the KunkelPeters model, please read
[Christian Peters thesis](https://github.com/mckunkel/FaultFinder/blob/master/FaultFinder/bachelorthesis_christian_peters.pdf)
After the presence of a hardware malfunction is classifed, a specific object detector, using a 4 layer (input, 2 hidden, output) deep neural network using mutil-labeld logistic regression to detect the placement of the fault.
The entire package has a minimum average preceision of ~97.5% for detector superlayers that have less then 5 grouped malfunctions, i.e. Channel_1 + Fuse_A+Connector_B...
The AI runs in the background as a recommendation system.
The AI and the FaultFinder have the capability to run within a Spark cluster with very small modifications to the base code.

Data used to train the classifiers was created using a simulation package, included in the package, written by M.C. Kunkel.
KunkelPetersModel was designed by M.C. Kunkel and Christian Peters.
Object Detector created by M.C. Kunkel.
DCFaultFinder interface designed by M.C. Kunkel
Intergration of package components done by M.C. Kunkel

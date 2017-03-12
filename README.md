# ppackSVM
Ppack SVM for apache Spark

To compile:

    sbt clean package


Cross 5-fold cross validation of parameters:

    spark-submit --packages amplab:spark-indexedrdd:0.4.0 target/scala-2.11/ppackubuntu_2.11-1.0.jar VALIDATION trainingfile lambda sigma iterations outputfile numfeatures
    
    
To train and test:

    spark-submit --packages amplab:spark-indexedrdd:0.4.0 target/scala-2.11/ppackubuntu_2.11-1.0.jar TEST trainingfile lambda sigma iterations outputfile numfeatures testingfile
    
Parameter description:

 - trainingfile: Path of the training set in libsvm format
 - lambda: Regularization Term
 - sigma: Kernel Parameter
 - iterations: Number of iterations
 - outputfile: log file
 - numfeatures: Number of variables of the dataset
 - testingfile: Path of the test set in libsvm format

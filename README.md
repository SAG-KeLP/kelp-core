kelp-core
=========
   
[**KeLP**][kelp-site] is the Kernel-based Learning Platform (Filice '15) developed in the [Semantic Analytics Group][sag-site] of
the [University of Roma Tor Vergata][uniroma2-site].

This is the **KeLP** core module and it contains the infrastructure of abstract classes and interfaces to work with KeLP. Furthermore, some implementations of algorithms, kernels and representations are included, to provide a base operative environment. More sophisticated components can be found in various extending modules, such as [kelp-additional-algorithms](https://github.com/SAG-KeLP/kelp-additional-algorithms) and [kelp-additional-kernels](https://github.com/SAG-KeLP/kelp-additional-kernels).

KELP is released as open source software under the Apache 2.0 license and the source code is available on [Github][github].

###**Core Structures**

Core functionalities of KeLP comprise the interfaces and abstract classes needed to build and extend the library.
The main interfaces and abstract classes are:

* 	*Dataset*: it models the notion of a dataset as a collection of examples
* 	*Example*: it models a single example as a collection of representations
* 	*Representation*: it is the base type for a generic representation
* 	*Label*: it models the label
* 	*Kernel*: it models the notion of kernel
* 	*LearningAlgorithm*: it is the base type for a learning algorithm
* 	*PredictionFunction*: it is the base type for a function that computes a prediction
* 	*Manipulator*: it is a class providing some methods to modify data and perform simple pre-processing steps 

###**Representations**
kelp-core include two vectorial representation that can be exploited in both linear and kernel-based learning models.

* _DenseVector_: it is a vectorial representation that should be adopted in modeling dense feature vectors in a small feature space, like an embedding. It relies on [EJML][ejml-site] for an efficient implementation. 

* _SparseVector_: it represents the best option for modeling sparse feature vector from high dimensional feature spaces, like a Bag-of-Words feature space. It relies on a hashmap implementation based on [TROVE][trove-site], in order to guarantee and efficient solution both from memory usage and computational perspectives. 

###**Learning Algorithms**

In this package different subclasses of the LearningAlgorithm interface can be found. The majority of the classes here is not an actual implementation, but they are used to build the hierarchy needed to instantiate the different kind of learning algorithms.
For example, *BinaryLearningAlgorithm* is responsible to model the notion of a learning algorithm that operates with two classes. *KernelMethod* instead is used to model the notion of learning algorithm based on Kernel functions (e.g., Support Vector Machines).

The following actual implementations of Learning Algorithms are included:

**CLASSIFICATION ALGORITHMS:**

* _BinaryCSvmClassification_: it is the KeLP implemention of C-Support Vector Machine learning algorithm. It is a learning algorithm for binary classification and it relies on kernel functions. It is a porting of the LibSVM implementation (Chang '11)
* _BinaryNuSvmClassification_: it is the KeLP implemention of &nu;-Support Vector Machine learning algorithm. It is a learning algorithm for binary classification and it relies on kernel functions. It is a porting of the LibSVM implementation (Chang '11)
* _OneClassSvmClassification_: the KeLP implemention of One-Class Support Vector Machine learning algorithm. It is a learning algorithm for estimating the Support of a High-Dimensional Distribution and it relies on kernel functions. The model is acquired only by considering positive examples. It is useful in anomaly detection (a.k.a. novelty detection). It is a porting of the LibSVM implementation (Chang '11)

**REGRESSION ALGORITHMS:**

* _EpsilonSvmRegression_: It implements the  &epsilon;-SVR learning algorithm discussed in (Chang '11)

**CLUSTERING ALGORITHMS:**

* _KernelBasedKMeansEngine_: it is the implemetation of the clustering algorithm described in (Kulis '09). It is basically a kernel-based extention of the standard k-mean clustering algorithm.


**META ALGORITHMS:**

* *OneVsAllLearningAlgorithm*: implementation of the One-Vs-All schema for extending binary classification algorithms to multi-class classification problems.
* *OneVsOneLearningAlgorithm*: implementation of the One-Vs-One schema for extending binary classification algorithms to multi-class classification problems.
* *MultiLabelClassificationLearning*: implementation of a multilabel learning strategy for extending binary classification algorithms to multi-label classification tasks.

###**Prediction Functions**

The *PredictionFunction* interface model the notion of function used to make a prediction. Different classes are subtype of *PredictionFunction* depending on the role they have in classification or regression schemas. For example, *BinaryClassifier* extends a *Classifier* that is a prediction function used to derive discrete classifications.

###**Kernel functions**

*Kernel* is the base type for modeling a kernel function. Subclasses of kernel model different type of kernel functions available.

**DirectKernel**

It models a kernel that operates directly on a specific representation (e.g., a linear kernel or a tree kernel extends this class)

* _LinearKernel_: it performs a dot product between explicit feature vectors, like _DenseVector_ or _SparseVector_.

**KernelComposition**

it models a kernel function that operates on the result produced by another kernel function. 

* _PolynomialKernel_: it applies the polynomial operation over the result of another kernel
* _RbfKernel_: it is the implementations of the Radial Basis Funtion Kernel (a.k.a. Gaussian Kernel)
* _NormalizationKernel_: it normalizes the result of another kernel making it ranging in [-1;1]

**KernelCombination:**

it models a kernel function that combines other kernel functions.

* _LinearKernelCombination_: it applies a weighted linear combination of kernels. The sum of two kernels corresponds to the concatenation of their respective feature spaces.
* _KernelMultiplication_: it multiplies the results of different kernels. The product of two kernels
corresponds to the Cartesian products of their feature spaces.

**KernelOnPairs:**

It is a kernel operating on instances of _ExamplePair_, i.e., examples naturally modeled as pairs, such as question and answer in Q/A, or text and hyphothesis in textual entailment. 

* _PreferenceKernel_: it is the implementation of the Preference Kernel proposed in (Shen '03) and largely used in lerning to rank tasks

* _PairwiseSumKernel_: it implements the following formula: K(<x1,x2>, <y1,y2>) = BK(x1, y1) + BK(x2, y2) + BK(x1, y2) + BK(x2, y1). Where BK is a base kernel. (See (Filice '15b))

* _PairwiseProductKernel_: it implements the following formula: K(<x1,x2>, <y1,y2>) = BK(x1, y1) * BK(x2, y2) + BK(x1, y2) * BK(x2, y1). Where BK is a base kernel. (See (Filice '15b))

* _UncrossedPairwiseSumKernel_: it implements the following formula: K(<x1,x2>, <y1,y2>) = BK(x1, y1) + BK(x2, y2). Where BK is a base kernel. (See (Filice '15b))

* _UncrossedPairwiseProductKernel_: it implements the following formula: K(<x1,x2>, <y1,y2>) = BK(x1, y1) * BK(x2, y2). Where BK is a base kernel. (See (Filice '15b))

* _BestPairwiseAlignmentKernel_: it implements the following formula: K(<x1,x2>, <y1,y2>) = softmax(BK(x1, y1) * BK(x2, y2), BK(x1, y2) * BK(x2, y1)). Where BK is a base kernel. (See (Filice '15b))


=============

##Including KeLP in your project

If you want to include the core functionalities of **KeLP** you can  easily include it in your [Maven][maven-site] project adding the following repositories to your pom file:

```
<repositories>
	<repository>
			<id>kelp_repo_snap</id>
			<name>KeLP Snapshots repository</name>
			<releases>
				<enabled>false</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<snapshots>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>fail</checksumPolicy>
			</snapshots>
			<url>http://sag.art.uniroma2.it:8081/artifactory/kelp-snapshot/</url>
		</repository>
		<repository>
			<id>kelp_repo_release</id>
			<name>KeLP Stable repository</name>
			<releases>
				<enabled>true</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>warn</checksumPolicy>
			</releases>
			<snapshots>
				<enabled>false</enabled>
				<updatePolicy>always</updatePolicy>
				<checksumPolicy>fail</checksumPolicy>
			</snapshots>
			<url>http://sag.art.uniroma2.it:8081/artifactory/kelp-release/</url>
		</repository>
	</repositories>
```

Then, the [Maven][maven-site] dependency for the kelp-core project is:

```
<dependency>
    <groupId>it.uniroma2.sag.kelp</groupId>
    <artifactId>kelp-core</artifactId>
    <version>2.0.0</version>
</dependency>
```

Alternatively, thanks to the modularity of **KeLP**, you can include one of the following modules that already contains the dependency to kelp-core:

* [kelp-additional-kernels](https://github.com/SAG-KeLP/kelp-additional-kernels): it contains additional kernel functions, such as the Tree Kernels or the Graph Kernels;

* [kelp-additional-algorithms](https://github.com/SAG-KeLP/kelp-additional-algorithms): it contains additional learning algorithms, such as the **KeLP** Java implementation of Liblinear or Online Learning algorithms, such as the Passive Aggressive;

* [kelp-full](https://github.com/SAG-KeLP/kelp-full): it is a complete package of KeLP that contains the entire set of existing modules, i.e. additional  kernel functions and algorithms.


=============

REFERENCES
-------------

(Chang '11) Chih-Chung Chang and Chih-Jen Lin. _LIBSVM: A
 library for support vector machines_. ACM Transactions on Intelligent Systems and Technology, 2:27:1-27:27, 2011. Original code available at [LibSVM][libsvm-site] 

(Filice '15) Simone Filice, Giuseppe Castellucci, Danilo Croce, Roberto Basili. _KeLP: a Kernel-based Learning Platform for Natural Language Processing_. In: Proceedings of ACL: System Demonstrations. Beijing, China (July 2015)

(Filice '15b) Simone Filice, Giovanni Da San Martino and Alessandro Moschitti. _Structural Representations for Learning Relations between Pairs of Texts_. In Proc. of ACL 2015.

(Kulis '09) Brian Kulis, Sugato Basu, Inderjit Dhillon, and Raymond Mooney. _Semi-supervised graph clustering: a kernel approach_. Machine Learning, 74(1):1-22, January 2009.

(Shen '03) L. Shen and A. K. Joshi. _An SVM based voting algorithm with application to parse reranking_. In Proc. of CoNLL. 2003

Useful Links
-------------

KeLP site: [http://sag.art.uniroma2.it/demo-software/kelp/][kelp-site]

SAG site: [http://sag.art.uniroma2.it][kelp-site]

Source code hosted at GitHub: [https://github.com/SAG-KeLP][github]


[sag-site]: http://sag.art.uniroma2.it "SAG site"
[uniroma2-site]: http://www.uniroma2.it "University of Roma Tor Vergata"
[maven-site]: http://maven.apache.org "Apache Maven"
[kelp-site]: http://sag.art.uniroma2.it/demo-software/kelp/ "KeLP website"
[ejml-site]: https://code.google.com/p/efficient-java-matrix-library/ "EJML site"
[trove-site]: http://trove.starlight-systems.com/news "TROVE site"
[github]: https://github.com/SAG-KeLP
[libsvm-site]: https://www.csie.ntu.edu.tw/~cjlin/libsvm/
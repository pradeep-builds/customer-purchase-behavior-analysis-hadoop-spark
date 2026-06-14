// Import Spark ML libraries
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

// Create target label (purchase=1, others=0)
val mlDF=cleanDF.withColumn("label",when(col("event_type")==="purchase",1).otherwise(0))

// Select features for prediction
val mlData=mlDF.select("product_id","category_id","brand","category_code","price","label")

// Balance dataset
val purchase=mlData.filter($"label"===1)
val nonPurchase=mlData.filter($"label"===0).sample(false,0.05,42)
val balanced=purchase.union(nonPurchase)

// Convert categorical columns to numerical indexes
val productIndexer=new StringIndexer().setInputCol("product_id").setOutputCol("productIndex").fit(balanced)
val categoryIndexer=new StringIndexer().setInputCol("category_id").setOutputCol("categoryIndex").fit(balanced)
val brandIndexer=new StringIndexer().setInputCol("brand").setOutputCol("brandIndex").fit(balanced)
val codeIndexer=new StringIndexer().setInputCol("category_code").setOutputCol("categoryCodeIndex").fit(balanced)

// Transform data
val d1=productIndexer.transform(balanced)
val d2=categoryIndexer.transform(d1)
val d3=brandIndexer.transform(d2)
val d4=codeIndexer.transform(d3)

// Create feature vector
val assembler=new VectorAssembler().setInputCols(Array("price","productIndex","categoryIndex","brandIndex","categoryCodeIndex")).setOutputCol("features")
val finalDF=assembler.transform(d4).select("features","label")

// Split data into train and test sets
val Array(train,test)=finalDF.randomSplit(Array(0.8,0.2),42)

// Train Random Forest model
val rf=new RandomForestClassifier().setLabelCol("label").setFeaturesCol("features").setNumTrees(100)
val model=rf.fit(train)

// Generate predictions
val predictions=model.transform(test)

// Evaluate accuracy
val accuracy=new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("accuracy").evaluate(predictions)
println("Accuracy = "+accuracy)

// Evaluate F1 score
val f1=new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("f1").evaluate(predictions)
println("F1 Score = "+f1)

// Display confusion matrix
predictions.groupBy("label","prediction").count().show(false)
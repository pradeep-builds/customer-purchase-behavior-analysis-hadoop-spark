// Import Spark SQL functions
import org.apache.spark.sql.functions._

// Load dataset from Hadoop HDFS into Spark DataFrame
val df=spark.read.option("header","true").csv("hdfs://localhost:9000/bigdata_project/customer_behavior.csv")

// Display dataset schema
df.printSchema()

// Display first 5 records
df.show(5,false)

// Display total number of records
println(df.count())

// Display total number of attributes
println(df.columns.length)
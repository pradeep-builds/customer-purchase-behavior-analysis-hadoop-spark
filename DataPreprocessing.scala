// Import Spark SQL functions
import org.apache.spark.sql.functions._

// Convert data types and replace "NULL" strings with actual null values
val df2=df.withColumn("event_time",to_timestamp(col("event_time"),"yyyy-MM-dd HH:mm:ss z")).withColumn("product_id",col("product_id").cast("long")).withColumn("category_id",col("category_id").cast("long")).withColumn("price",col("price").cast("double")).withColumn("user_id",col("user_id").cast("long")).withColumn("category_code",when(col("category_code")==="NULL",null).otherwise(col("category_code"))).withColumn("brand",when(col("brand")==="NULL",null).otherwise(col("brand")))

// Verify updated schema
df2.printSchema()

// Count missing values in each column
df2.select(df2.columns.map(c=>sum(col(c).isNull.cast("int")).alias(c)):_*).show(false)

// Remove records with missing brand and category information
val cleanDF=df2.na.drop(Seq("brand","category_code"))

// Display record count after cleaning
println(cleanDF.count())
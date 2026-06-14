// Import Spark SQL functions
import org.apache.spark.sql.functions._

// Event type distribution
cleanDF.groupBy("event_type").count().show(false)

// Top 10 brands by customer interactions
cleanDF.groupBy("brand").count().orderBy(desc("count")).show(10,false)

// Top 10 product categories
cleanDF.groupBy("category_code").count().orderBy(desc("count")).show(10,false)

// Total purchase transactions
println(cleanDF.filter(col("event_type")==="purchase").count())

// Top purchased brands
cleanDF.filter(col("event_type")==="purchase").groupBy("brand").count().orderBy(desc("count")).show(10,false)

// Revenue analysis
cleanDF.filter(col("event_type")==="purchase").agg(format_number(sum("price"),2).alias("Total Revenue")).show(false)

// Most active users
cleanDF.groupBy("user_id").count().orderBy(desc("count")).show(10,false)
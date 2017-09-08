mvn compile exec:java -Dexec.mainClass=org.sinmetal.metaltile.land.initializer.LandInitializer \
     -Dexec.args="--runner=DataflowRunner --project=metal-tile-dev1 \
     --tempLocation=gs://dataflow-work-metal-tile-dev1/tmp" \
     -Pdataflow-runner
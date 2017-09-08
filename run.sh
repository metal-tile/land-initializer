mvn compile exec:java -Dexec.mainClass=org.sinmetal.metaltile.land.initializer.LandInitializer \
     -Dexec.args="--runner=DataflowRunner --project=metal-tile-dev1 \
     --tempLocation=gs://dataflow-work-metal-tile-dev1/tmp \
     --bigQueryTable=metal-tile-dev1:world_default.land_home \
     --datastoreKind=world-default20170907-land-home" \
     -Pdataflow-runner
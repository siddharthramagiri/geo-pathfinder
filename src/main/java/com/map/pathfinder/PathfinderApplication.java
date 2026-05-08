package com.map.pathfinder;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.config.Profile;
import com.graphhopper.util.CustomModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
})
public class PathfinderApplication {

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("--import-graph-only")) {
			runImportOnly();
			return;
		}
		SpringApplication.run(PathfinderApplication.class, args);
	}

	private static void runImportOnly() {
		GraphHopperConfig config = new GraphHopperConfig();
		config.putObject("graph.dataaccess", "MMAP");
		config.putObject("graph.location", System.getenv().getOrDefault("GRAPH_CACHE_PATH", "graph-cache"));
		config.putObject("datareader.file", System.getenv().getOrDefault("OSM_FILE_PATH", "data/telangana-latest.osm.pbf"));
		config.putObject("import.osm.ignored_highways", "elevator,bus_guideway,raceway,proposed,planned,abandoned,platform,construction");

		Profile profile = new Profile("car")
				.setVehicle("car")
				.setWeighting("custom")
				.setCustomModel(new CustomModel());

		config.setProfiles(List.of(profile));
		config.setCHProfiles(List.of());
		config.setLMProfiles(List.of());

		GraphHopper hopper = new GraphHopper();
		hopper.init(config);

		System.out.println("Importing OSM and building graph cache...");
		hopper.importOrLoad();
		System.out.println("Done. Nodes: " + hopper.getBaseGraph().getNodes());
		hopper.close();
	}
}

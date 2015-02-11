package com.techlooper.enricher.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.techlooper.enricher.Enricher;
import com.techlooper.utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * Created by phuonghqh on 2/11/15.
 */
public abstract class AbstractEnricher implements Enricher {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEnricher.class);

  protected String configPath;

  protected String folder;

  protected String failListPath;

  protected JsonNode config;

  protected JsonNode appConfig;

  protected ExecutorService executorService;

  protected DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss").withLocale(Locale.US);

  protected String techlooperFolder;

  protected String apiFolder;

  public void postTechlooper(String source, JsonNode users) {
    String url = config.at("/techlooper/apiUrl").asText();
    String jsonPath = String.format("%s%s.json", techlooperFolder, source);
    try {
      if (new File(source).exists()) {
        jsonPath = source;
      }
      Unirest.post(url).body(users.toString()).asString().getStatus();
      Utils.writeToFile(users, String.format("%s.ok", jsonPath));
    }
    catch (Exception e) {
      LOGGER.error("Not post to {}, error: {}", url, e);
      try {
        Utils.writeToFile(users, jsonPath);
      }
      catch (IOException ex) {
        LOGGER.error("ERROR", ex);
      }
    }
  }

  public void initialize(ExecutorService executorService, String configPath, JsonNode appConfig) throws IOException {
    LOGGER.debug("Initialize AboutMeEnricher with config {}", configPath);
    try {
      this.executorService = executorService;
      this.appConfig = appConfig;
      this.config = Utils.parseJson(new File(configPath));
      this.configPath = configPath;

      prepareProperties();
    }
    catch (Exception e) {
      LOGGER.error("ERROR", e);
      Utils.writeToFile(config, this.configPath);
    }
  }

  private void prepareProperties() {
    folder = this.appConfig.get("folder").asText();
    failListPath = String.format("%s%s.txt", folder, dateTimeFormatter.print(DateTime.now()));

    techlooperFolder = folder + config.at("/techlooper/folderName").asText();
    apiFolder = folder + config.at("/api/folderName").asText();
    Utils.sureFolder(techlooperFolder, apiFolder);
  }

  public JsonNode getConfig() {
    return config;
  }
}

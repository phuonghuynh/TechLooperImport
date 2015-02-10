package com.techlooper.imports;

import com.techlooper.pojo.VietnamworksUser;
import com.techlooper.repository.VietnamworksUserRepository;
import com.techlooper.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * Created by NguyenDangKhoa on 2/9/15.
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "com.techlooper")
public class VietnamworksUserImport {

  private static Logger LOGGER = LoggerFactory.getLogger(VietnamworksUserImport.class);

  private static VietnamworksUserRepository vietnamworksUserRepository;

  public static void main(String[] args) throws Throwable {
    SpringApplication app = new SpringApplication(VietnamworksUserImport.class);
    ApplicationContext applicationContext = app.run();
    vietnamworksUserRepository = applicationContext.getBean("vietnamworksUserRepository", VietnamworksUserRepository.class);
    String enrichUserAPI = applicationContext.getEnvironment().getProperty("githubUserProfileEnricher.techlooper.api.enrichUser");

    final int totalUsers = vietnamworksUserRepository.getTotalUser();
    final int pageSize = 100;
    final int numberOfPages = totalUsers % pageSize == 0 ? totalUsers / pageSize : totalUsers / pageSize + 1;
    int pageIndex = 0;

    while (pageIndex < 2) {
      List<Long> resumes = vietnamworksUserRepository.getResumeList(pageIndex * pageSize, pageSize);
      Optional<String> vietnamworksUsers = Utils.toJSON(vietnamworksUserRepository.getUsersByResumeId(resumes));

      if (vietnamworksUsers.isPresent()) {
        int result = Utils.postJsonString(enrichUserAPI, vietnamworksUsers.get());
        LOGGER.info("Imported user in page #" + pageIndex + " successfully.");
        if (result != 204) {
          LOGGER.info("Import user in page #" + pageIndex + " fail.");
        }
      }
    }
  }

}

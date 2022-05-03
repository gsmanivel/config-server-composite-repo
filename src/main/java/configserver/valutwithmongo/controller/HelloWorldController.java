package configserver.valutwithmongo.controller;

import configserver.valutwithmongo.configuration.CompositeEnvironmentRepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.server.environment.vault.SpringVaultEnvironmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class HelloWorldController {

    @Autowired
    CompositeEnvironmentRepositoryConfiguration compositeRepo;

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }


    @PostMapping("/secret")
    public void createSecret(@RequestParam String strPath, @RequestBody Object obj ){
        compositeRepo.getVaultRepoObjectToWrite().getKeyValueTemplate().put(strPath,obj);
    }

    @PatchMapping("/secret")
    public void updateSecret(@RequestParam String strPath, @RequestBody Map<String, ?> var2){
        compositeRepo.getVaultRepoObjectToWrite().getKeyValueTemplate().patch(strPath,var2);
    }
}

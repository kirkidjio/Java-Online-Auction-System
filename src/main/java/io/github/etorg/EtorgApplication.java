package io.github.etorg;

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RestController
@SpringBootApplication
public class EtorgApplication {
    
    @RequestMapping("/")
    String home(){
        return "Hello World!";
    }

	public static void main(String[] args) {
		SpringApplication.run(EtorgApplication.class, args);
	}

}

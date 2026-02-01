package com.musicPlay.music_play;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusicPlayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicPlayApplication.class, args);
	}

}

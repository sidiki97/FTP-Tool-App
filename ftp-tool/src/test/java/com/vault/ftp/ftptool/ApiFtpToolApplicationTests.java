package com.vault.ftp.ftptool;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ApiFtpToolApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
		String osname = System.getProperty("os.name");
		float fileSize = 10419337;
		if (!((System.getProperty("os.name").substring(0,3)) == "Mac")){
			fileSize = (fileSize / (1024*1024)) * 1000 * 1000;
		}

		int filesize = (int)fileSize;
		System.out.println(osname);
	}


}

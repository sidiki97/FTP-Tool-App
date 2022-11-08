package com.vault.ftp.ftptool;


import com.vault.ftp.ftptool.Models.Authentication;
import com.vault.ftp.ftptool.Models.DeleteFTP;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import com.veeva.vault.vapil.api.model.response.FileStagingItemResponse;
import com.veeva.vault.vapil.api.model.response.FileStagingJobResponse;
import com.veeva.vault.vapil.api.model.response.UserPermissionResponse;
import com.veeva.vault.vapil.api.model.response.VaultResponse;
import com.veeva.vault.vapil.api.request.DocumentRenditionRequest;
import com.veeva.vault.vapil.api.request.DocumentRequest;
import com.veeva.vault.vapil.api.request.FileStagingRequest;
import com.veeva.vault.vapil.api.request.UserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@SpringBootTest
class ApiFtpToolApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) throws IOException {
		VaultClientId vaultClientId = new VaultClientId("verteobiotech","vault","quality",true,"myintegration");
		// Instantiate the VAPIL VaultClient using user name and password authentication
		VaultClient vaultClient = VaultClientBuilder
				.newClientBuilder(VaultClient.AuthenticationType.BASIC)
				.withVaultDNS("supportvaults-platform-gr-vv1-1077-community.veevavault.com")
				.withVaultUsername("abbas.siddiqui@supportvaults.com")
				.withVaultPassword("V@ultWork23")
				.withVaultClientId(vaultClientId)
				.build();


		Path path1 = Paths.get("/Users/abbassiddiqui-mbpr16/Downloads/system_audit_history_as_of_2022_11_04_EDT.pdf");

		byte[] arr1 = Files.readAllBytes(path1);
		byte[] arr2 = Files.readAllBytes(path1);



		FileStagingItemResponse fileStagingItemResponse = vaultClient.newRequest(FileStagingRequest.class)
				.setFile("Source_2004_v_0.1.pdf", arr1)
				.setOverwrite(false)
				.createFolderOrFile(FileStagingRequest.Kind.FILE, "u8790933/test3.pdf");

		FileStagingItemResponse fileStagingItemResponse2 = vaultClient.newRequest(FileStagingRequest.class)
				.setFile("Source_2004_v_0.1.pdf", arr2)
				.setOverwrite(false)
				.createFolderOrFile(FileStagingRequest.Kind.FILE, "u8790933/test4.pdf");

	}


}

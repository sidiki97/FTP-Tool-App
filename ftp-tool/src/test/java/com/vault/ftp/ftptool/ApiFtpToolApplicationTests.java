package com.vault.ftp.ftptool;


import com.vault.ftp.ftptool.Models.Authentication;
import com.vault.ftp.ftptool.Models.DeleteFTP;
import com.veeva.vault.vapil.api.client.VaultClient;
import com.veeva.vault.vapil.api.client.VaultClientBuilder;
import com.veeva.vault.vapil.api.client.VaultClientId;
import com.veeva.vault.vapil.api.model.response.FileStagingJobResponse;
import com.veeva.vault.vapil.api.request.FileStagingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ApiFtpToolApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
//		String osname = System.getProperty("os.name");
//		float fileSize = 10419337;
//		if (!((System.getProperty("os.name").substring(0,3)) == "Mac")){
//			fileSize = (fileSize / (1024*1024)) * 1000 * 1000;
//		}
//
//		int filesize = (int)fileSize;
//		System.out.println(osname);

		VaultClientId vaultClientId = new VaultClientId("Veeva", "Vault",
				"Platform", true, "FTP App");

		VaultClient vaultClient;

		Authentication authentication = new Authentication();
		authentication.setPassWord("V@ultWork23");
		authentication.setUserName("abbas.siddiqui@supportvaults.com");
		authentication.setVaultDNS("supportvaults-platform-lr-vv2-2087-community.veevavault.com");

		vaultClient = VaultClientBuilder
				.newClientBuilder(VaultClient.AuthenticationType.BASIC)
				.withVaultDNS(authentication.getVaultDNS())
				.withVaultUsername(authentication.getUserName())
				.withVaultPassword(authentication.getPassWord())
				.withVaultClientId(vaultClientId)
				.build();

		DeleteFTP deleteFTP = new DeleteFTP();

		deleteFTP.setFtpPath("u8790933");
		deleteFTP.setRecursive(true);
		FileStagingJobResponse fileStagingJobResponse = vaultClient.newRequest(FileStagingRequest.class)
				.setRecursive(deleteFTP.getRecursive())
				.deleteFolderOrFile(deleteFTP.getFtpPath());

		System.out.println(fileStagingJobResponse.getData().toString());


	}


}

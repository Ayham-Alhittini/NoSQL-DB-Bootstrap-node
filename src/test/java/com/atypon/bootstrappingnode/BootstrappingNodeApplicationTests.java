package com.atypon.bootstrappingnode;


import com.atypon.bootstrappingnode.util.DataEncryptor;
import org.junit.Test;

public class BootstrappingNodeApplicationTests {

	@Test
	public void testEncryptionDecryption() throws Exception {
		System.out.println(DataEncryptor.decrypt("lrDCAv25HI5LrJCKQSK2X_Xmb4JfesNYhVPRezL1BZdnh9uwpdp-b3fua0WeGWFC"));
	}
}

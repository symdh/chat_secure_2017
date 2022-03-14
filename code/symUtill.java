public class symUtill {
	private int encryptS; 
	private byte[] byte_key;
	private byte[] byte_kIV;
	private boolean devel_mod; 

	// 초기화1
	public symUtill () {

		String key = "aes256-test-key!";
		String kIV = "hws56$#4";

		this.byte_key = key.getBytes();
		this.byte_kIV =  kIV.getBytes();
	}

	// 초기화2
	public symUtill (String key, String kIV) {
		this.byte_key = key.getBytes();
		this.byte_kIV =  kIV.getBytes();;
	}

	public void S_devel_mod (boolean devel_mod) {
		this.devel_mod = devel_mod;
	}

	public void S_encrypt_mod (int encryptS) {
		this.encryptS = encryptS;
	}

	public byte[] key_value() {
		return this.byte_key ;
	}

	public byte[] kIV_value() {
		return this.byte_kIV ;
	}

	//바이트 단위 출력 & 설명
   static void print_byte(byte[] result, String explain) {
         
       System.out.print(explain +" : ");      

       for (int i = 0; i < result.length; i++)
          System.out.print(result[i]+" ");

       System.out.println("");          
    } 

     public String sym_encrypt (String msg) {
     	boolean devel_mod = this.devel_mod;
     	int encryptS = this.encryptS;
     	byte[] encrypt_result = null;
       byte[] decrypt_result = null;
       String temp_result = null;
       String result = null;

       if(devel_mod) System.out.println("==============개발모드=============");
     	if(devel_mod) System.out.println("바이트 전환전 평문 : " + msg);

     	byte[] byte_msg = msg.getBytes();
     	if(devel_mod) print_byte(byte_msg, "암호화 하기전 바이트 ");

     	switch (encryptS){
   	     	case 1:
   	     		encrypt_result =  KISA_HIGHT_ECB.HIGHT_ECB_Encrypt(key_value(),byte_msg,0,byte_msg.length);
   	     		break;
   	     	case 2:
   	     		encrypt_result = KISA_HIGHT_CBC.HIGHT_CBC_Encrypt(key_value(), kIV_value(), byte_msg, 0, byte_msg.length);
   	     		break;
   	     	case 3:
   	     		encrypt_result = KISA_HIGHT_CTR.HIGHT_CTR_Encrypt(key_value(), kIV_value(), byte_msg, 0, byte_msg.length);
   	     		break;
   	     	default:
     	}

     	if(devel_mod) print_byte(encrypt_result, "암호화 후 바이트 ");
     	if(devel_mod) System.out.println("================================");

     	result = Base64.encode(encrypt_result);
     	return result;
     }

     public String sym_decrypt (String msg) {
     	boolean devel_mod = this.devel_mod;
     	int encryptS = this.encryptS;
     	byte[] encrypt_result = null;
      byte[] decrypt_result = null;
      String temp_result = null;
      String result = null;

      byte[] byte_msg = Base64.decode(msg);
       if(devel_mod) System.out.println("==============개발모드=============");
     	if(devel_mod) print_byte(byte_msg, "정상통신여부 바이트 출력");

     	temp_result = new String(byte_msg);
     	if(devel_mod) System.out.println("복호화하기전 평문전환 :" +temp_result);

     	switch (encryptS){
   	     	case 1:
   	     		decrypt_result =  KISA_HIGHT_ECB.HIGHT_ECB_Decrypt(key_value(),byte_msg,0,byte_msg.length);
   	     		break;
   	     	case 2:
   	     		decrypt_result = KISA_HIGHT_CBC.HIGHT_CBC_Decrypt(key_value(), kIV_value(), byte_msg, 0, byte_msg.length);
   	     		break;
   	     	case 3:
   	     		decrypt_result = KISA_HIGHT_CTR.HIGHT_CTR_Decrypt(key_value(), kIV_value(), byte_msg, 0, byte_msg.length);
   	     		break;
   	     	default:
     	}

     	if(devel_mod) print_byte(decrypt_result, "복호화 후 바이트 ");

     	result = new String(decrypt_result);
      if(devel_mod) System.out.println("복호화후 바이트 평문전환 :" +result);
      if(devel_mod) System.out.println("================================");

     	return result;
     }
}
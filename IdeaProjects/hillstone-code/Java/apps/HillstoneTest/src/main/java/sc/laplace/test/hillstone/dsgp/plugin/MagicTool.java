package sc.laplace.test.hillstone.dsgp.plugin;


/**
 * @author jxwu
 */
public class MagicTool {

    public static final int INT_256 = 256;
    int[] sbox = new int[INT_256];
    int a = 0;
    int b = 0;
    int t = 0;

    public MagicTool(String publicKey) {
        int keyLen = publicKey.length();

        for (int i = 0; i < INT_256; i++) {
            sbox[i] = i;
        }

        int j = 0;
        int[] k = new int[INT_256];
        for (int i = 0; i < INT_256; i++) {
            k[i] = publicKey.charAt(i % keyLen);
        }

        for (int i = 0; i < INT_256; i++) {
            j = (j + sbox[i] + k[i]) % INT_256;
            int tmp = sbox[i];
            sbox[i] = sbox[j];
            sbox[j] = tmp;
        }
    }

    /**
     * 分批解密
     */
    public byte[] rc4(byte[] bytes, int start, int len) {
        byte[] resultBytes = new byte[len];
        for (int kk = 0; kk < len; kk++) {
            a = (a + 1) % INT_256;
            b = (b + sbox[a]) % INT_256;
            int tmp = sbox[a];
            sbox[a] = sbox[b];
            sbox[b] = tmp;
            t = (sbox[a] + sbox[b]) % INT_256;
            resultBytes[kk] = (byte) (bytes[kk + start] ^ sbox[t]);
        }

        return resultBytes;
    }
}

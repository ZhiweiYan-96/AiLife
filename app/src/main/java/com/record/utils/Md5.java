package com.record.utils;

public class Md5 {
    static final byte[] PADDING = new byte[]{Byte.MIN_VALUE, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
    static final int S11 = 7;
    static final int S12 = 12;
    static final int S13 = 17;
    static final int S14 = 22;
    static final int S21 = 5;
    static final int S22 = 9;
    static final int S23 = 14;
    static final int S24 = 20;
    static final int S31 = 4;
    static final int S32 = 11;
    static final int S33 = 16;
    static final int S34 = 23;
    static final int S41 = 6;
    static final int S42 = 10;
    static final int S43 = 15;
    static final int S44 = 21;
    private byte[] buffer = new byte[64];
    private long[] count = new long[2];
    private byte[] digest = new byte[16];
    public String digestHexStr;
    private long[] state = new long[4];

    public String getMD5ofStr(String inbuf) {
        md5Init();
        md5Update(inbuf.getBytes(), inbuf.length());
        md5Final();
        this.digestHexStr = "";
        for (int i = 0; i < 16; i++) {
            this.digestHexStr += byteHEX(this.digest[i]);
        }
        return this.digestHexStr;
    }

    public Md5() {
        md5Init();
    }

    private void md5Init() {
        this.count[0] = 0;
        this.count[1] = 0;
        this.state[0] = 1732584193;
        this.state[1] = 4023233417L;
        this.state[2] = 2562383102L;
        this.state[3] = 271733878;
    }

    private long F(long x, long y, long z) {
        return (x & y) | ((-1 ^ x) & z);
    }

    private long G(long x, long y, long z) {
        return (x & z) | ((-1 ^ z) & y);
    }

    private long H(long x, long y, long z) {
        return (x ^ y) ^ z;
    }

    private long I(long x, long y, long z) {
        return ((-1 ^ z) | x) ^ y;
    }

    private long FF(long a, long b, long c, long d, long x, long s, long ac) {
        a += (F(b, c, d) + x) + ac;
        return ((long) ((((int) a) << ((int) s)) | (((int) a) >>> ((int) (32 - s))))) + b;
    }

    private long GG(long a, long b, long c, long d, long x, long s, long ac) {
        a += (G(b, c, d) + x) + ac;
        return ((long) ((((int) a) << ((int) s)) | (((int) a) >>> ((int) (32 - s))))) + b;
    }

    private long HH(long a, long b, long c, long d, long x, long s, long ac) {
        a += (H(b, c, d) + x) + ac;
        return ((long) ((((int) a) << ((int) s)) | (((int) a) >>> ((int) (32 - s))))) + b;
    }

    private long II(long a, long b, long c, long d, long x, long s, long ac) {
        a += (I(b, c, d) + x) + ac;
        return ((long) ((((int) a) << ((int) s)) | (((int) a) >>> ((int) (32 - s))))) + b;
    }

    private void md5Update(byte[] inbuf, int inputLen) {
        int i;
        byte[] block = new byte[64];
        int index = ((int) (this.count[0] >>> 3)) & 63;
        long[] jArr = this.count;
        long j = jArr[0] + ((long) (inputLen << 3));
        jArr[0] = j;
        if (j < ((long) (inputLen << 3))) {
            jArr = this.count;
            jArr[1] = jArr[1] + 1;
        }
        jArr = this.count;
        jArr[1] = jArr[1] + ((long) (inputLen >>> 29));
        int partLen = 64 - index;
        if (inputLen >= partLen) {
            md5Memcpy(this.buffer, inbuf, index, 0, partLen);
            md5Transform(this.buffer);
            i = partLen;
            while (i + 63 < inputLen) {
                md5Memcpy(block, inbuf, 0, i, 64);
                md5Transform(block);
                i += 64;
            }
            index = 0;
        } else {
            i = 0;
        }
        md5Memcpy(this.buffer, inbuf, index, i, inputLen - i);
    }

    private void md5Final() {
        byte[] bits = new byte[8];
        Encode(bits, this.count, 8);
        int index = ((int) (this.count[0] >>> 3)) & 63;
        md5Update(PADDING, index < 56 ? 56 - index : 120 - index);
        md5Update(bits, 8);
        Encode(this.digest, this.state, 16);
    }

    private void md5Memcpy(byte[] output, byte[] input, int outpos, int inpos, int len) {
        for (int i = 0; i < len; i++) {
            output[outpos + i] = input[inpos + i];
        }
    }

    private void md5Transform(byte[] block) {
        long a = this.state[0];
        long b = this.state[1];
        long c = this.state[2];
        long d = this.state[3];
        long[] x = new long[16];
        Decode(x, block, 64);
        a = FF(a, b, c, d, x[0], 7, 3614090360L);
        d = FF(d, a, b, c, x[1], 12, 3905402710L);
        c = FF(c, d, a, b, x[2], 17, 606105819);
        b = FF(b, c, d, a, x[3], 22, 3250441966L);
        a = FF(a, b, c, d, x[4], 7, 4118548399L);
        d = FF(d, a, b, c, x[5], 12, 1200080426);
        c = FF(c, d, a, b, x[6], 17, 2821735955L);
        b = FF(b, c, d, a, x[7], 22, 4249261313L);
        a = FF(a, b, c, d, x[8], 7, 1770035416);
        d = FF(d, a, b, c, x[9], 12, 2336552879L);
        c = FF(c, d, a, b, x[10], 17, 4294925233L);
        b = FF(b, c, d, a, x[11], 22, 2304563134L);
        a = FF(a, b, c, d, x[12], 7, 1804603682);
        d = FF(d, a, b, c, x[13], 12, 4254626195L);
        c = FF(c, d, a, b, x[14], 17, 2792965006L);
        b = FF(b, c, d, a, x[15], 22, 1236535329);
        a = GG(a, b, c, d, x[1], 5, 4129170786L);
        d = GG(d, a, b, c, x[6], 9, 3225465664L);
        c = GG(c, d, a, b, x[11], 14, 643717713);
        b = GG(b, c, d, a, x[0], 20, 3921069994L);
        a = GG(a, b, c, d, x[5], 5, 3593408605L);
        d = GG(d, a, b, c, x[10], 9, 38016083);
        c = GG(c, d, a, b, x[15], 14, 3634488961L);
        b = GG(b, c, d, a, x[4], 20, 3889429448L);
        a = GG(a, b, c, d, x[9], 5, 568446438);
        d = GG(d, a, b, c, x[14], 9, 3275163606L);
        c = GG(c, d, a, b, x[3], 14, 4107603335L);
        b = GG(b, c, d, a, x[8], 20, 1163531501);
        a = GG(a, b, c, d, x[13], 5, 2850285829L);
        d = GG(d, a, b, c, x[2], 9, 4243563512L);
        c = GG(c, d, a, b, x[7], 14, 1735328473);
        b = GG(b, c, d, a, x[12], 20, 2368359562L);
        a = HH(a, b, c, d, x[5], 4, 4294588738L);
        d = HH(d, a, b, c, x[8], 11, 2272392833L);
        c = HH(c, d, a, b, x[11], 16, 1839030562);
        b = HH(b, c, d, a, x[14], 23, 4259657740L);
        a = HH(a, b, c, d, x[1], 4, 2763975236L);
        d = HH(d, a, b, c, x[4], 11, 1272893353);
        c = HH(c, d, a, b, x[7], 16, 4139469664L);
        b = HH(b, c, d, a, x[10], 23, 3200236656L);
        a = HH(a, b, c, d, x[13], 4, 681279174);
        d = HH(d, a, b, c, x[0], 11, 3936430074L);
        c = HH(c, d, a, b, x[3], 16, 3572445317L);
        b = HH(b, c, d, a, x[6], 23, 76029189);
        a = HH(a, b, c, d, x[9], 4, 3654602809L);
        d = HH(d, a, b, c, x[12], 11, 3873151461L);
        c = HH(c, d, a, b, x[15], 16, 530742520);
        b = HH(b, c, d, a, x[2], 23, 3299628645L);
        a = II(a, b, c, d, x[0], 6, 4096336452L);
        d = II(d, a, b, c, x[7], 10, 1126891415);
        c = II(c, d, a, b, x[14], 15, 2878612391L);
        b = II(b, c, d, a, x[5], 21, 4237533241L);
        a = II(a, b, c, d, x[12], 6, 1700485571);
        d = II(d, a, b, c, x[3], 10, 2399980690L);
        c = II(c, d, a, b, x[10], 15, 4293915773L);
        b = II(b, c, d, a, x[1], 21, 2240044497L);
        a = II(a, b, c, d, x[8], 6, 1873313359);
        d = II(d, a, b, c, x[15], 10, 4264355552L);
        c = II(c, d, a, b, x[6], 15, 2734768916L);
        b = II(b, c, d, a, x[13], 21, 1309151649);
        a = II(a, b, c, d, x[4], 6, 4149444226L);
        d = II(d, a, b, c, x[11], 10, 3174756917L);
        c = II(c, d, a, b, x[2], 15, 718787259);
        b = II(b, c, d, a, x[9], 21, 3951481745L);
        long[] jArr = this.state;
        jArr[0] = jArr[0] + a;
        jArr = this.state;
        jArr[1] = jArr[1] + b;
        jArr = this.state;
        jArr[2] = jArr[2] + c;
        jArr = this.state;
        jArr[3] = jArr[3] + d;
    }

    private void Encode(byte[] output, long[] input, int len) {
        int i = 0;
        for (int j = 0; j < len; j += 4) {
            output[j] = (byte) ((int) (input[i] & 255));
            output[j + 1] = (byte) ((int) ((input[i] >>> 8) & 255));
            output[j + 2] = (byte) ((int) ((input[i] >>> 16) & 255));
            output[j + 3] = (byte) ((int) ((input[i] >>> 24) & 255));
            i++;
        }
    }

    private void Decode(long[] output, byte[] input, int len) {
        int i = 0;
        for (int j = 0; j < len; j += 4) {
            output[i] = ((b2iu(input[j]) | (b2iu(input[j + 1]) << 8)) | (b2iu(input[j + 2]) << 16)) | (b2iu(input[j + 3]) << 24);
            i++;
        }
    }

    public static long b2iu(byte b) {
        return b < (byte) 0 ? (long) (b & 255) : (long) b;
    }

    public static String byteHEX(byte ib) {
        char[] Digit = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        return new String(new char[]{Digit[(ib >>> 4) & 15], Digit[ib & 15]});
    }

    public static String toMd5(String source) {
        return new Md5().getMD5ofStr(source).toLowerCase();
    }
}

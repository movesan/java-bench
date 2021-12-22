package appliedalgorithm;

/**
 * @description: twitter的snowflake算法 -- java实现
 * @author: movesan
 * @create: 2020-04-19 21:02
 **/
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    /**
     * 每一部分的最大值
     * 拿 MACHINE_BIT 举例说明：
     *      5位（bit）可以表示的最大正整数是 2^5 - 1 = 31，即可以用0、1、2、3、....31这32个数字，来表示不同的 MACHINE_BIT
     *      -1L ^ (-1L << 5L)结果是31，2^5 - 1 的结果也是31，所以在代码中，-1L ^ (-1L << 5L)的写法是利用位运算计算出5位能表示的最大正整数是多少
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT); // 31
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT); // 31
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT); // 4095

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastStmp = -1L;//上一次时间戳

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            /*
              long seqMask = -1L ^ (-1L << 12L); //计算12位能耐存储的最大正整数，相当于：2^12-1 = 4095
                 System.out.println("seqMask: "+seqMask);
                 System.out.println(1L & seqMask);
                 System.out.println(2L & seqMask);
                 System.out.println(3L & seqMask);
                 System.out.println(4L & seqMask);
                 System.out.println(4095L & seqMask);
                 System.out.println(4096L & seqMask);
                 System.out.println(4097L & seqMask);
                 System.out.println(4098L & seqMask);

                 seqMask: 4095
                 1
                 2
                 3
                 4
                 4095
                 0
                 1
                 2

                这段代码通过位与运算保证计算的结果范围始终是 0-4095 ！
             */
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        /*
             1  |                    41                        |  5  |   5  |     12
               0|0001100 10100010 10111110 10001001 01011100 00|     |      |              //la
               0|                                              |10001|      |              //lb
               0|                                              |     |1 1001|              //lc
            or 0|                                              |     |      |‭0000 00000000‬ //sequence
            ------------------------------------------------------------------------------------------
               0|0001100 10100010 10111110 10001001 01011100 00|10001|1 1001|‭0000 00000000‬ //结果：910499571847892992

            纵向观察发现:
                在41位那一段，除了la一行有值，其它行（lb、lc、sequence）都是0，（我爸其它）
                在左起第一个5位那一段，除了lb一行有值，其它行都是0
                在左起第二个5位那一段，除了lc一行有值，其它行都是0
                按照这规律，如果sequence是0以外的其它值，12位那段也会有值的，其它行都是0
            横向观察发现:
                在la行，由于左移了5+5+12位，5、5、12这三段都补0了，所以la行除了41那段外，其它肯定都是0
                同理，lb、lc、sequnece行也以此类推
                正因为左移的操作，使四个不同的值移到了SnowFlake理论上相应的位置，然后四行做位或运算（只要有1结果就是1），就把4段的二进制数合并成一个二进制数。

            左移运算是为了将数值移动到对应的段(41、5、5，12那段因为本来就在最右，因此不用左移)。
            然后对每个左移后的值(la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数。
            最后转换成10进制，就是最终生成的id
         */
        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
                | datacenterId << DATACENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        for (int i = 0; i < 30; i++) {
            System.out.println(snowFlake.nextId());
        }

    }
}

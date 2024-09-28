package rs.v9.myeconomy.claim;

public enum Flags {

    EXPLOSION_ALLOWED {
        @Override
        public byte getByteValue(){
            return 0x01;
        }
    }, NONE;

    public byte getByteValue(){
        return 0x00;
    }

    public static Flags fromByteValue(byte value){
        for(Flags flag : values()){
            if(flag.getByteValue() == value){
                return flag;
            }
        }

        return NONE;
    }
}

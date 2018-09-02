package com.shxy.www.util;

public class ObjectUtil {
    
	/**
     * 将对象转换为Int
     * @param obj 需要转换的对象
     * @return
     * 		返回转换成功后的long(转换不成	: 0)
     */
    public static int obj2Int(Object obj){
        return (int) obj2Long(obj);
    }
    
    /**
     * 将对象转换为Long
     * @param obj 需要转换的对象
     * @return
     * 		返回转换成功后的long(转换不成	: 0)
     */
    public static long obj2Long(Object obj){
    	long num = 0;
    	if(obj==null){
    		return 0;
    	}
    	if(obj instanceof Integer){//如果对象是整形
    		return ((Integer)obj).intValue();
    	}
    	if(obj instanceof Float){//对象是浮点型
    		return ((Float)obj).intValue();
    	}
    	if(obj instanceof Double){//对象是double
    		return ((Double)obj).intValue();
    	}
    	if(obj instanceof Character){//对象是一个字符
    		return ((Character)obj).charValue();
    	}
    	if(obj instanceof Long){//对象是长整型
    		return ((Long)obj).intValue();
    	}
    	if(obj instanceof Short){//对象是短整形
    		return ((Short)obj).intValue();
    	}
    	if(obj instanceof Boolean){//对象是布朗型
    		return ((Boolean)obj)?1:0;
    	}
    	if(obj instanceof Byte){//对象是一个字节
    		return ((Byte)obj).intValue();
    	}
    	if(obj instanceof String||obj instanceof StringBuilder||obj instanceof StringBuffer){//其他类型
    		try {
    			num = Long.parseLong(obj.toString());
    		} catch (Exception e) {}
    	}
    	return num;
    }
    
    /**
     * 判断数组是否为空
     * @param args 比较的对象数组
     * @return
     * 		true = 数组为null
     * 		false= 数组不为
     */
    public static boolean arrIsNULL(Object ... args){
        if(args==null){
            return true;
        }
        for(Object obj:args){
            if(obj==null){//对象为空
                return true;
            }else if(obj instanceof String){//如果是字符串 判断长度是否为0
                String str=obj.toString();
                if(str.trim().length()==0){
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 比较两个对象是否相等
     * @param obj1
     * @param obj2
     * @return 
     * 		true	: 如果两个对象相等或相等
     * 		false	: 两个对象不等
     */
    public static boolean compare2Obj(Object obj1, Object obj2){
        if(arrIsNULL(obj1, obj2)){
            return false;
        }
        if ((obj1 instanceof Integer)&&(obj2 instanceof Integer)) {//两个对象是整形
            if((Integer)obj1==(Integer)obj2){
                return true;
            }
        }else if((obj1 instanceof Float)&&(obj2 instanceof Float)){//两个对象是浮点型
        	if((Float)obj1==(Float)obj2){
                return true;
            }
        }else if((obj1 instanceof Double)&&(obj2 instanceof Double)){
        	if((Double)obj1==(Double)obj2){
                return true;
            }
        }else if((obj1 instanceof Long)&&(obj2 instanceof Long)){
        	if((Long)obj1==(Long)obj2){
                return true;
            }
        }else if((obj1 instanceof Character)&&(obj2 instanceof Character)){
        	if((Character)obj1==(Character)obj2){
                return true;
            }
        }else if((obj1 instanceof Short)&&(obj2 instanceof Short)){
        	if((Short)obj1==(Short)obj2){
                return true;
            }
        }else{
            if(obj1.toString().equals(obj2.toString())){
                return true;
            }
        }
        return false;
    }
    
    /**
     * 比较两个数
     * @param
     * 		number1 参与比较的数1
     * 		number2 参与比较的数2
     * @return 
     * 		true = number1>=number2
     * 		false= number1<number2
     */
    public static boolean comp2Nums(Object number1, Object number2){
        if(number1==null||number2==null){
            return false;
        }
        int num1=0;
        int num2=0;
        try {
            num1 = obj2Int(number1);
            num2 = obj2Int(number2);
            if(num1>=num2){
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
    
	/**
	 * 获得Object对象的字符串
	 * @param obj 需要转换的对象
	 * @return
	 * 		obj的字符串形式(如果obj为空，返回空字符串)
	 */
	public static String obj2Str(Object obj){
		if(obj==null){
			return "";
		}
		return obj.toString();
	}
	
	/**
	 * 判断对象是否为空
	 * @param params
	 * @return
	 * 		true = 对象为空
	 * 		false= 对象不为空
	 */
	public static boolean objIsNull(Object... params){
		if(params==null||params.length<=0){
			return true;
		}
		for(Object obj: params){
			if(obj==null){
				return true;
			}
			if(obj instanceof String){//对象是字符串要比较是否为空
				if(StringUtil.arrIsNULL(obj)){
					return true;
				}
			}
		}
		return false;
	}
}

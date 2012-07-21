package clojure.lang;
import java.lang.reflect.Method;
public final class RIC extends AFn implements IType, IConstant
{
public final int arity;
public final String methodName;
public volatile int state;
public Method method;
public final static IPersistentVector getBasis()
{
return RT.vector(Symbol.intern("arity"), Symbol.intern("methodName"));
}
public Class getConstantType(){ return RIC.class; 
}
public RIC(Object arity, Object methodName)
{
this.arity=(Integer)arity;
this.methodName=(String)methodName;
this.state=0;
}
public Object invoke(Object arg0)
{
if (this.arity == 1) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[0];
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[0];
Class[] t = method.getParameterTypes();
 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[0];
Class[] t = method.getParameterTypes();
 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[0];
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[0];
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(1);
}return null;
}
public Object invoke(Object arg0,Object arg1)
{
if (this.arity == 2) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[1];oo[0] = arg1; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[1];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[1];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[1];o[0] = arg1; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[1];o[0] = arg1; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(2);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2)
{
if (this.arity == 3) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[2];oo[0] = arg1; oo[1] = arg2; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[2];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[2];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[2];o[0] = arg1; o[1] = arg2; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[2];o[0] = arg1; o[1] = arg2; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(3);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3)
{
if (this.arity == 4) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[3];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[3];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[3];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[3];o[0] = arg1; o[1] = arg2; o[2] = arg3; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[3];o[0] = arg1; o[1] = arg2; o[2] = arg3; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(4);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4)
{
if (this.arity == 5) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[4];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[4];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[4];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[4];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[4];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(5);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5)
{
if (this.arity == 6) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[5];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[5];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[5];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[5];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[5];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(6);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6)
{
if (this.arity == 7) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[6];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[6];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[6];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[6];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[6];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(7);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7)
{
if (this.arity == 8) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[7];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[7];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[7];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[7];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[7];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(8);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8)
{
if (this.arity == 9) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[8];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[8];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[8];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[8];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[8];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(9);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9)
{
if (this.arity == 10) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[9];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[9];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[9];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[9];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[9];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(10);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10)
{
if (this.arity == 11) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[10];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[10];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[10];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[10];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[10];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(11);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11)
{
if (this.arity == 12) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[11];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[11];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[11];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[11];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[11];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(12);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12)
{
if (this.arity == 13) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[12];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[12];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[12];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[12];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[12];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(13);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13)
{
if (this.arity == 14) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[13];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[13];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[13];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[13];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[13];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(14);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14)
{
if (this.arity == 15) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[14];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[14];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[14];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[14];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[14];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(15);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15)
{
if (this.arity == 16) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[15];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; oo[14] = arg15; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[15];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[15];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[15];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[15];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(16);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16)
{
if (this.arity == 17) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[16];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; oo[14] = arg15; oo[15] = arg16; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[16];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[16];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[16];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[16];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(17);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17)
{
if (this.arity == 18) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[17];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; oo[14] = arg15; oo[15] = arg16; oo[16] = arg17; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[17];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[17];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[17];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[17];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(18);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Object arg18)
{
if (this.arity == 19) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[18];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; oo[14] = arg15; oo[15] = arg16; oo[16] = arg17; oo[17] = arg18; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[18];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); o[17] = Reflector.boxArg(t[17],arg18); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[18];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); o[17] = Reflector.boxArg(t[17],arg18); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[18];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; o[17] = arg18; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[18];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; o[17] = arg18; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(19);
}return null;
}
public Object invoke(Object arg0,Object arg1,Object arg2,Object arg3,Object arg4,Object arg5,Object arg6,Object arg7,Object arg8,Object arg9,Object arg10,Object arg11,Object arg12,Object arg13,Object arg14,Object arg15,Object arg16,Object arg17,Object arg18,Object arg19)
{
if (this.arity == 20) {
if (state == 0 || state == 1) {

    if (state == 0) {
      System.out.println("cold cache");
 Object[] oo = new Object[19];oo[0] = arg1; oo[1] = arg2; oo[2] = arg3; oo[3] = arg4; oo[4] = arg5; oo[5] = arg6; oo[6] = arg7; oo[7] = arg8; oo[8] = arg9; oo[9] = arg10; oo[10] = arg11; oo[11] = arg12; oo[12] = arg13; oo[13] = arg14; oo[14] = arg15; oo[15] = arg16; oo[16] = arg17; oo[17] = arg18; oo[18] = arg19; 
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {

Object[] o = new Object[19];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); o[17] = Reflector.boxArg(t[17],arg18); o[18] = Reflector.boxArg(t[18],arg19); 
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println("warm cache");

Object[] o = new Object[19];
Class[] t = method.getParameterTypes();
 o[0] = Reflector.boxArg(t[0],arg1); o[1] = Reflector.boxArg(t[1],arg2); o[2] = Reflector.boxArg(t[2],arg3); o[3] = Reflector.boxArg(t[3],arg4); o[4] = Reflector.boxArg(t[4],arg5); o[5] = Reflector.boxArg(t[5],arg6); o[6] = Reflector.boxArg(t[6],arg7); o[7] = Reflector.boxArg(t[7],arg8); o[8] = Reflector.boxArg(t[8],arg9); o[9] = Reflector.boxArg(t[9],arg10); o[10] = Reflector.boxArg(t[10],arg11); o[11] = Reflector.boxArg(t[11],arg12); o[12] = Reflector.boxArg(t[12],arg13); o[13] = Reflector.boxArg(t[13],arg14); o[14] = Reflector.boxArg(t[14],arg15); o[15] = Reflector.boxArg(t[15],arg16); o[16] = Reflector.boxArg(t[16],arg17); o[17] = Reflector.boxArg(t[17],arg18); o[18] = Reflector.boxArg(t[18],arg19); 
      try {
        Object r = method.invoke(arg0, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals("object is not an instance of declaring class")){
          state=2;

System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    }  Object[] o = new Object[19];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; o[17] = arg18; o[18] = arg19; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
} else {
 Object[] o = new Object[19];o[0] = arg1; o[1] = arg2; o[2] = arg3; o[3] = arg4; o[4] = arg5; o[5] = arg6; o[6] = arg7; o[7] = arg8; o[8] = arg9; o[9] = arg10; o[10] = arg11; o[11] = arg12; o[12] = arg13; o[13] = arg14; o[14] = arg15; o[15] = arg16; o[16] = arg17; o[17] = arg18; o[18] = arg19; 
System.out.println("failed cache");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);

        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);
}
} else {
super.throwArity(20);
}return null;
}
}

/*

(let [max-arity 20
      init-state 0
      prep-ret "
        if (r == null)
          return null;
        Class c = r.getClass();
        return Reflector.prepRet(c,r);"
      
      failed-cache (str "
System.out.println(\"failed cache\");
Object r = Reflector.invokeInstanceMethod(arg0, methodName, o);
" prep-ret)]
  (println "package clojure.lang;")
  (println "import java.lang.reflect.Method;")
  (println "public final class RIC extends AFn implements IType, IConstant")
  (println "{")
  (println "public final int arity;")
  (println "public final String methodName;")
  (println "public volatile int state;")
  (println "public Method method;")
  (println "public final static IPersistentVector getBasis()")
  (println "{")
  (println "return RT.vector(Symbol.intern(\"arity\"), Symbol.intern(\"methodName\"));")
  (println "}")
  (println "public Class getConstantType(){"
           "return RIC.class;"
           "\n}")
  (println "public RIC(Object arity, Object methodName)")
  (println "{")
  (println "this.arity=(Integer)arity;")
  (println "this.methodName=(String)methodName;")
  (printf "this.state=%s;\n" init-state)
  (println "}")
  (doseq  [i (range 1 (inc max-arity))
           :let [args (for [a (range i)]
                        (format "Object arg%s" a))
                 arg-array (str "
Object[] o = new Object[" (dec i) "];
Class[] t = method.getParameterTypes();
 "
(apply str (for [ii (range 1 i)]    ; arg array setup
                 (format "o[%s] = Reflector.boxArg(t[%s],arg%s); "
                         (dec ii)
                         (dec ii)
                         ii))))]]
    (printf "public Object invoke(%s)\n" (apply str (interpose \, args)))
    (println "{")
    (printf "if (this.arity == %s) {\n%s\n} else {\nsuper.throwArity(%s);\n}"
            i
            (format "if (state == 0 || state == 1) {\n%s\n} else {\n%s\n}"
                    (str
    "
    if (state == 0) {
      System.out.println(\"cold cache\");
"
(str " Object[] oo = new Object[" (dec i) "];"
     (apply str (for [ii (range 1 i)] ; arg array setup
                  (format "oo[%s] = arg%s; " (dec ii) ii))))
"
      method = Reflector.findInstanceMethod(arg0, methodName, oo);
      oo=null;
      try {
"
arg-array
"
        Object r = method.invoke(arg0, (Object[])Util.ret1(o,o=null));
" prep-ret "
      } catch(Exception e) {
        if(e.getCause() instanceof Exception)
          throw Util.sneakyThrow(e.getCause());
        else if(e.getCause() instanceof Error)
          throw (Error) e.getCause();
        throw Util.sneakyThrow(e);
      } finally {
        state=1;
      }
    } else if(state == 1) {
      System.out.println(\"warm cache\");
"
arg-array
"
      try {
        Object r = method.invoke(arg0, o);
" prep-ret "
      } catch (java.lang.reflect.InvocationTargetException e){
        state=2;
" failed-cache "
      } catch(IllegalArgumentException e){
        if (e.getMessage().equals(\"object is not an instance of declaring class\")){
          state=2;
" failed-cache "
        } else {
          throw e;
        }
      } catch(Exception e) {
        e.printStackTrace();
        throw Util.sneakyThrow(e);
      }
    } " (str " Object[] o = new Object[" (dec i) "];"
                         (apply str (for [ii (range 1 i)] ; arg array setup
                                      (format "o[%s] = arg%s; " (dec ii) ii)))    
                         failed-cache))
(str " Object[] o = new Object[" (dec i) "];"
                         (apply str (for [ii (range 1 i)] ; arg array setup
                                      (format "o[%s] = arg%s; " (dec ii) ii)))    
                         failed-cache))
            
            
  i)
    
    (println "return null;\n}"))
  (println "}"))

 */

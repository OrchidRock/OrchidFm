package fileOperate;
import java.io.*;
import java.util.*;
/**
 * 该类主要是用来处理用户从命令行输入的字符流
 * 并提供服务给FileManger类
 * @version 1.0 2015-10-20
 * @author rock
 *
 */
public class CmdLineReader {
	/*
	 * 单元测试
	 */
	/*
	public static void main(String[] args){
		CmdLineReader reader=new CmdLineReader();
		reader.getNextCmdLine();
		reader.close();
	}
	*/
	// 该字符串数组存储命令的各个阶段
	private String[] cmdLineStrings; 
	//输入流读入器
	private Scanner in;
	
	/*Constructor
	 * 初始化输入流读入器为标准输入
	 */
	public CmdLineReader(){
		//cmdLineStrings=null;
		in=new Scanner(System.in);
	}
	/*
	 * 关闭输入流读入器
	 */
   public void close(){
	   in.close();
   }
	public String[] getNextCmdLine(){
		String test;
		if((test=in.nextLine())==null){
			return null;
		}
		cmdLineStrings=test.split(" ");
		return cmdLineStrings;
	}
	
}

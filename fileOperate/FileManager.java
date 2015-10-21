package fileOperate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.nio.file.Paths;
import java.io.File;

/**
 * 命令行文件管理器
 * @version 1.0  2015-10-20
 * @version 1.1  2015-10-21
 * @author rock
 *
 */
public class FileManager {
	//static final String rootName="Rock-JavaCmd$:";
	//static private String[] cmdLine;
	public static void main(String[] args) throws IOException{
		MainLoop mainloop=new MainLoop();
		mainloop.startMainLoop();
	}
}
/**
 * 新建目录类
 * 注意该类和下面负责具体功能的类一样，不对输入格式是否正确进行处理
 * 输入格式的检错由MainLoop类完成
 */
class CreateNewFloder{
	CreateNewFloder(String[] token)throws IOException{
		String currentDir=System.getProperty("user.dir");
		System.out.println(currentDir);
		if(token.length==3)//带另外路径
		{
			if(token[2].charAt(0)=='/')//绝对路径
				currentDir=token[2];
			else//相对路径
				currentDir=currentDir+"/"+token[2];
		}
		//File file=new File(currentDir);
		//System.out.println(currentDir);
		//System.out.println(file.getAbsolutePath());
		Path path=Paths.get(currentDir);
		if(path!=null){
			String name=token[1];
			File f=new File(currentDir+File.separator+name);//这里试着使用Files类，如果这里使用Files类，那么在其他地方也使用该类
			f.mkdir();
		    //System.out.println("目录"+currentDir+"无效！");
		}else{
			System.out.println("路径错误！");
		}
	}
}

/*
 * 控制整个程序运行的主类
 */
class MainLoop{
	private  String rootName="Rock-Orchid>～";
	private static final String rootPath=System.getProperty("user.dir");//最原始的根路径
	private String[] cmdLineToken;
	private Scanner in=new Scanner(System.in);
	public void startMainLoop() throws IOException{
		while(true){
			System.out.print(rootName+": ");
			cmdLineToken=in.nextLine().split(" ");//Brug1 这里命令间的 空格数不能超过1
			int countToken=cmdLineToken.length;
			//System.out.println("countToken="+countToken);
			if(countToken<1){
				System.out.println("输入格式错误");
				continue;
			}
			switch(cmdLineToken[0]){
			case "-help":
				BufferedReader helpIn=new BufferedReader(new InputStreamReader(new FileInputStream("help.txt")));
				String helpLine;
				while((helpLine=helpIn.readLine())!=null)
					System.out.println(helpLine);
				helpIn.close();
				break;
			case "-mkdir":
				new CreateNewFloder(cmdLineToken);
				break;
			case "-lo":
				enterNextFloder(cmdLineToken[1]);
				break;
			case "-rm":                //删除
				deleteFileOrFloder(cmdLineToken);
				break;
			default:
					System.out.println(cmdLineToken[0]+".. 未找到命令");
					break;
			}
		}
		
	}
	/**
	 * 删除某个文件或目录
	 * 不提供删除绝对路径文件或目录的服务
	 * @param Token
	 */
	private void deleteFileOrFloder(String[] token) throws IOException{
		// 判断删除的对象是文件夹还是文件
		if(token[1].equals("-r") && token.length>2){//对象是目录
			Path path=Paths.get(System.getProperty("user.dir"),token[2]);
			//System.out.println(path.toString());
			Files.isDirectory(path, null);
			Files.delete(path);
			
		}
		else{
			//Path path2=Paths.get(System.getProperty("user.dir"),token[1]);
			
		}
	}
	/*
	 * 返回父目录
	 */
	@Deprecated
	private void backParentFloder() {
		// TODO Auto-generated method stub
	    String currentDir=System.getProperty("user.dir");
	    File file=new File(currentDir);
	   System.out.println( "父目录"+file.getParent());
	    
	}
	
	
	/*
	 * 进入下一个指定的目录
	 */
	private  void enterNextFloder(String floderName){
		String currentDir=System.getProperty("user.dir");
		if(floderName.charAt(0)=='/'){//绝对路径
			currentDir=floderName;
			if(!(new File(currentDir)).exists()){
				System.out.println("目录"+currentDir+"不存在");
				return;
			}
			rootName=rootName.substring(0, 12)+" "+currentDir;//更新rootName
		}else if(floderName.charAt(0)=='~'){
			currentDir=rootPath;
			rootName=rootName.substring(0,12)+" ~ ";
		}
		else{
			String appendOfRootName="";
			if(floderName.equals("..")){//处理 floderName=".."的情况，此时返回到父目录
				File file=new File(currentDir);
				currentDir=file.getParent();
				appendOfRootName=" "+currentDir;
				rootName=rootName.substring(0,12);
			}else{
				currentDir=currentDir+"/"+floderName;
				appendOfRootName="/"+floderName;
			}
			
			if(!(new File(currentDir)).exists()){
				System.out.println("相对目录"+currentDir+"不存在");
				return;
			}
			rootName=rootName+appendOfRootName;
		}
		System.setProperty("user.dir",currentDir);//修改当前根路径
		
	}
	
	
	
	
	
}

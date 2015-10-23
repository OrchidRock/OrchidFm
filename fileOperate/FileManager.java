package fileOperate;

import java.io.*;
import java.security.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
/**
 * 命令行文件管理器
 * @version 1.0  2015-10-20
 * @version 1.1  2015-10-21
 * @version 1.2  2015-10-22
 * @author rock
 *
 */
public class FileManager {
	//static final String rootName="Rock-JavaCmd$:";
	//static private String[] cmdLine;
	public static void main(String[] args) throws Exception{
		MainLoop mainloop=new MainLoop();
		mainloop.startMainLoop();
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

	public void startMainLoop() throws Exception {
		while (true) {
			System.out.print(rootName + ": ");
			cmdLineToken = in.nextLine().split(" ");// Brug1 这里命令间的 空格数不能超过1
			int countToken = cmdLineToken.length;
			// System.out.println("countToken="+countToken);
			if (countToken < 1) {
				System.out.println("输入格式错误");
				continue;
			}
			switch (cmdLineToken[0]) {
			case "-help":
				BufferedReader helpIn = new BufferedReader(new InputStreamReader(new FileInputStream(".help.txt")));
				String helpLine;
				while ((helpLine = helpIn.readLine()) != null)
					System.out.println(helpLine);
				helpIn.close();
				break;
			case "-mkdir":
				createNewFloder(cmdLineToken);
				break;
			case "-lo":
				if (cmdLineToken.length > 1)
					enterNextFloder(cmdLineToken[1]);// 这里未对Token[1]是否存在没有检测，下面也是
				break;
			case "-rm": // 删除
				DedeleteFileOrFloder deer = new DedeleteFileOrFloder();
				deer.deleteFileOrFloder(cmdLineToken);
				break;
			case "-ls":
				watchAllChildNode(cmdLineToken);
				break;
			case "-copy":
				if (cmdLineToken.length < 3) {
					System.out.println(".. 命令格式有误，请查看-help");
					break;
				}
				new CopyFileOrFloder().copyFileOrFloder(cmdLineToken[1], cmdLineToken[2]);
				break;
			case "-cp":
				new CompressBook().compressToZip(cmdLineToken);
				break;
			case "-dec":
				DesEnCipher des=new DesEnCipher("I love The World");
				des.encipher(cmdLineToken[1], cmdLineToken[2]);
				break;
			case "-enc":
				DesEnCipher des2=new DesEnCipher("I love The World");
				des2.decipher(cmdLineToken[1], cmdLineToken[2]);
				break;
			default:
				System.out.println(cmdLineToken[0] + ".. 未找到命令");
				break;
			}
		}

	}
	/**
	 * @param newPath 目标目录
	 * @param sourcePath 源文件路径
	 */
	class CopyFileOrFloder{
		
	private void copyFileOrFloder(String oldPath, String newPath) throws IOException{
		// TODO Auto-generated method stub
		if(oldPath==null || newPath==null)
			return;
		//这里需要改进，尝试使用createNewFloder方法
		(new File(newPath)).mkdirs();
		String sourcePath=oldPath;
		System.out.println("oldPath1:"+oldPath);
		File source=null;String[] files=null;
		
		sourcePath=userPathChecker(System.getProperty("user.dir"), sourcePath);
		if(sourcePath==null){
			System.out.println("源目录或文件不存在");
			return;
		}
		if((source=new File(sourcePath)).isFile()){
			files=new String[1];
			int i=0;
			for(i=sourcePath.length()-1;sourcePath.charAt(i)!=File.separatorChar
					&&i>0;i--)
				;
			files[0]=sourcePath.substring(i+1,sourcePath.length());
			sourcePath=source.getParent();	
			System.out.println("files[0]:"+files[0]);
			System.out.println("sourcePath"+sourcePath);
		}else{
			source=new File(sourcePath);
			files=source.list();
		}
		File temp=null;
		for(String childFile:files){
			if(sourcePath.endsWith(File.separator))
				temp=new File(sourcePath+childFile);
			else
				temp=new File(sourcePath+File.separator+childFile);
			if(temp.isFile()){
				FileInputStream input=new FileInputStream(temp);
				FileOutputStream output=new FileOutputStream(newPath+File.separator+
				                               temp.getName());
				copyStream(input,output);
				input.close();
				output.flush();
				output.close();
			}
			if(temp.isDirectory())//DSP
				copyFileOrFloder(temp.toString(),newPath+File.separator+childFile);
		}
	}
	}
   public void copyStream(InputStream in,OutputStream out) throws IOException{
	   
	   byte[] b=new byte[1024*5];
	   int len;
	   while((len=in.read(b))!=-1)
		   out.write(b,0,len);
   }
	/*
	 * 
	 */
	private  String userPathChecker(String currentDir,String userPath){
		String temp=currentDir;
		if(temp==null)
			return null;
		char pixChar=userPath.charAt(0);
		if(pixChar==File.separatorChar)       //没有判断为空的情况
			temp=userPath;
		else if(pixChar=='~'){
                  String append="";
                  if(userPath.length()>1)
                        append=userPath.substring(1, userPath.length());
                  temp=rootPath+append;
                  //System.out.println("append:"+append);
		}
		else if(userPath.equals("..")){
			temp=(new File(currentDir)).getParent();
		}
		else 
			temp=temp+File.separator+userPath;
		//System.out.println("temp:"+temp);
		File file=new File(temp);
		if(file.isDirectory() || file.isFile() )
		     return temp;
		else{
		     return null;
		 }
	}
	/*
	 * 新建目录
	 */
	private void createNewFloder(String[] token) {
		
		if(token.length<2)
			return;
		String currentDir=System.getProperty("user.dir");
		//String name=null;
		if(token.length>2)//带另外路径
		{
			currentDir=userPathChecker(currentDir,token[2]);
			//System.out.println(currentDir);
			if(currentDir==null){
				System.out.println(token[2]+"..不存在该目录");
				return;
			}
			if((new File(currentDir)).isFile()){
				System.out.println(token[2]+"..是文件，不是目录");
				return;
			}
			 //name=token[1];	
			currentDir=currentDir+File.separator+token[1];
		}else{//如果-mkdir后面直接跟的是路径
			currentDir=token[1];
		}
		
		File f=new File(currentDir);//这里试着使用Files类，如果这里使用Files类，那么在其他地方也使用该类
		f.mkdir();
		    //System.out.println("目录"+currentDir+"无效！");
	}
	
	/**
	 * 删除某个文件或目录
	 * MainLoop的子类
	 * @param Token
	 */
class DedeleteFileOrFloder{
	
	//DedeleteFileOrFloder(){}
	public  void deleteFileOrFloder(String[] token) throws IOException{
		//System.out.println("I am here");
		if(token.length<2)
			return;
		String currentDir=null;
		if(token.length>2 && token[1].equals("-r")){
				currentDir=userPathChecker(System.getProperty("user.dir"),token[2]);
				if(currentDir==null){
					System.out.println(token[2]+"..目录不存在");
					return;
				}
				if((new File(currentDir)).isFile()){
					System.out.println(token[2]+"..是文件，不是目录");
					return;
				}
				deleteFileTree(new File(currentDir));
		}
		else{
			     currentDir=userPathChecker(System.getProperty("user.dir"),token[1]);
			     if(currentDir==null){
			    	 System.out.println(token[1]+"..文件或目录不存在");
			    	 return;
			     }
			     File file=new File(currentDir);
			     if(file.isDirectory()){
			    	 System.out.println(currentDir+"..是目录,请加上子命令 -r删除");
			    	 return;
			     }
			     file.delete();
			}	
		}
	   private  void deleteFileTree(File file){
		   File filelist[]=file.listFiles();
		   int listlen=filelist.length;
		   for(int i=0;i<listlen;i++){
			   if(filelist[i].isDirectory())
				   deleteFileTree(filelist[i]);
			   filelist[i].delete();
		   }
		   file.delete();
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
		   // String currentDir=System.getProperty("user.dir");
			String currentDir=userPathChecker(System.getProperty("user.dir"),floderName);
			if(currentDir==null){
				System.out.println(floderName+"..不存在该目录");
				return;
			}
			if((new File(currentDir)).isFile()){
				System.out.println(floderName+"..是文件，不是目录");
				return;
			}
			//更新rootName;
			//rootPath.;
			//System.out.println(currentDir);
			int len=Math.min(currentDir.length(), rootPath.length());
			if((currentDir.substring(0, len).equals(rootPath)))
				rootName=rootName.substring(0,12)+" ~"+
			                           currentDir.substring(len,currentDir.length());
			else
				rootName=rootName.substring(0,12)+" "+currentDir;
		
		    System.setProperty("user.dir",currentDir);//修改当前根路径
		
	}
	/*
	 * 查看当前目录下的所有子目录和文件
	 */
	private void watchAllChildNode(String[] token) {
		// TODO Auto-generated method stub
		String path=System.getProperty("user.dir");
		if(token.length>1){
		     path=userPathChecker(path,token[1]);
		     if(path==null || (new File(path)).isFile()){
					System.out.println(token[1]+"..不存在该目录");
					return;
			}
		    
		}
		if(!path.endsWith(File.separator))
			path=path+File.separator;
		File dirFile=new File(path);
		String[] files=dirFile.list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String path) {
				// TODO Auto-generated method stub
				String filename=new File(path).getName();
				return filename.charAt(0)!='.';
			}
			
		});
		for(String name:files)
			System.out.println(name);
	}
public class CompressBook{
	public CompressBook(){}
	public void compressToZip(String[] token) throws IOException{
		if(token.length<2)
			return;
		String goalPath=null;boolean isHasName=false;
		String goalZipPath=null;
		String sourcePath=userPathChecker(System.getProperty("user.dir"),token[1]);
		if(sourcePath==null){
			System.out.println(token[1]+"..目录或文件不存在");
			return;
		}
		//So stupid!!!!!!!!!
		if(token.length>2){
			if(token[token.length-2].equals("-n")){
				if(token.length==4){
					goalPath=System.getProperty("user.dir");
					goalZipPath=goalPath+File.separator+token[3]+".zip";
					isHasName=true;
				}
				else if(token.length==5){
					goalPath=userPathChecker(System.getProperty("user.dir"),token[2]);
					goalZipPath=goalPath+File.separator+token[4]+".zip";
					isHasName=true;
				}
			}
			else
				goalPath=userPathChecker(System.getProperty("user.dir"),token[2]);
		}else//默认 路径
			goalPath=System.getProperty("user.dir");
		
		if(goalPath==null || (new File(goalPath)).isFile()){
			System.out.println("..目录不存在");
			return;
		}
		
		if(!isHasName){
			int i=0;
			for(i=sourcePath.length()-1;sourcePath.charAt(i)!=File.separatorChar
					&&i>0;i--)
				;
			goalZipPath=goalPath+File.separator+
					sourcePath.substring(i+1, sourcePath.length())+".zip";
		}
		
		System.out.println("goalPath:"+goalZipPath);
		ZipOutputStream out=new ZipOutputStream(new FileOutputStream(goalZipPath));
		File inputFile=new File(sourcePath);
		Zip(out,inputFile,"");
		out.close();
	}
	public void Zip(ZipOutputStream out, File f, String base) throws IOException {
		// TODO Auto-generated method stub
		if(f.isDirectory()){
			File[] fl=f.listFiles();
		    out.putNextEntry(new ZipEntry(base+File.separator));
		    base=base.length()==0 ? "":base+File.separator;
		    for(File fChild:fl)
		    	Zip(out,fChild,base+fChild.getName());
		}else{
			out.putNextEntry(new ZipEntry(base));
			InputStream in=new FileInputStream(f);
			byte[] b=new byte[1024];int len=0;
			while((len=in.read(b))!=-1)
				out.write(b, 0, len);
			in.close();
		}
	}
}
class DesEnCipher{
	Key key;
    DesEnCipher(String str) throws NoSuchAlgorithmException{
		getKey(str);
	}
	public void getKey(String str) throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		KeyGenerator generator=KeyGenerator.getInstance("DES");
		generator.init(new SecureRandom(str.getBytes()));
		this.key=generator.generateKey();
		generator=null;
	}
	public void encipher(String file,String detfile)throws Exception{
		file=userPathChecker(System.getProperty("user.dir"),file);
		if(file==null){
			System.out.println(file+"..文件不存在");
			return;
		}
		Cipher cipher=Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		
		OutputStream out=new FileOutputStream(detfile);
		CipherInputStream cis=new CipherInputStream(new FileInputStream(file),cipher);
		byte[] buffer=new byte[1024];
		int r;
		while((r=cis.read(buffer))!=-1)
			out.write(buffer,0,r);
		cis.close();
		out.close();
	}
	public void decipher(String file,String detfile)throws Exception{
		file=userPathChecker(System.getProperty("user.dir"),file);
		if(file==null){
			System.out.println(file+"..文件不存在");
			return;
		}
		Cipher cipher=Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		
		InputStream in=new FileInputStream(file);
		CipherOutputStream cos=new CipherOutputStream(new FileOutputStream(detfile),cipher);
		byte[] buffer=new byte[1024];
		int r;
		while((r=in.read(buffer))!=-1)
			cos.write(buffer,0,r);
		cos.close();
		in.close();
	}
}
	
	
}

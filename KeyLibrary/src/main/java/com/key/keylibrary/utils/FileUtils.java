package com.key.keylibrary.utils;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.key.keylibrary.base.ConstantValues;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * created by key  on 2019/5/4
 */
public class FileUtils {

    public static void getImageUrl(Context context, final Activity activity){
        if(!FileUtils.isSDCard()){
            Toast.makeText(context,"当前路径不可用",Toast.LENGTH_LONG).show();
        }else{
            new Thread(){
                @Override
                public void run() {
                    Uri mImgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver cr = activity.getContentResolver();
                    Cursor cursor = cr.query(mImgUri,
                            null,
                            MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?",
                            new String[]{"image/jpeg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED);


                    Set<String> mDirPaths = new HashSet<>();
                    List<String> mImages = new ArrayList<>();
                    while (cursor.moveToNext()){
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        mImages.add(path);
                        File parentFile  = new File(path).getParentFile();
                        String dirPath = parentFile.getAbsolutePath();
                    }

                    cursor.close();
                }
            }.start();
        }

    }


    public static boolean isSDCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    /**
     *    读取assets下的文件
     * @param context
     * @param fileName
     * @return
     */
    public static String readAssetsFile(Context context, String fileName,String charset){
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer, charset);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }


    public static HashMap<String,String> getBankCard(Context context, String fileName) {
        String myjson = readAssetsFile(context, fileName,"utf-8");
        Gson gson = new Gson();
        HashMap hashMap = gson.fromJson(myjson, HashMap.class);
        //json转换为集合
        return hashMap;
    }


    public static void saveBitmap(String picName,Bitmap bm) {
        File f = new File(ConstantValues.DOWNLOAD, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static  String getImageToString(String filePhat) {
        String headimage = null;

        Bitmap photo = BitmapFactory.decodeFile(filePhat);

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] b = stream.toByteArray();
            headimage = Base64.encodeToString(b, Base64.DEFAULT);
            stream.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return headimage;
    }


    /**
     *    String 2 ASCI
     * @param value
     * @return
     */

    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }


    /**
     *    ASCI 2 String
     * @param value
     * @return
     */
    public static String asciiToString(String value)
    {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }


    /**
     *     获取版本号
     * @param context
     * @return
     */
    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }


    /**
     *    获取版本名称
     * @param context
     * @return
     */
    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }



    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        File file = new File(ConstantValues.DOWNLOAD + fileName);
        if (file.exists()){
            checker.checkDelete(file.toString());
            if (file.isFile()) {
                try {
                    file.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        }else
            status = false;
        return status;
    }



    /**
     * 取得压缩包中的 文件列表(文件夹,文件自选)
     *
     * @param zipFileString 压缩包名字
     * @param bContainFolder 是否包括 文件夹
     * @param bContainFile 是否包括 文件
     * @return
     * @throws Exception
     */
    public static List<File> GetFileList(String zipFileString, boolean bContainFolder, boolean bContainFile) throws Exception {
        List<File> fileList = new ArrayList<File>();
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
        java.util.zip.ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }

            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }//end of while

        inZip.close();

        return fileList;
    }

    /**
     * 返回压缩包中的文件InputStream
     *
     * @param zipFileString 压缩文件的名字
     * @param fileString 解压文件的名字
     * @return InputStream
     * @throws Exception
     */
    public static InputStream UpZip(String zipFileString, String fileString) throws Exception {
        //    android.util.Log.v("XZip", "UpZip(String, String)");
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFileString);
        java.util.zip.ZipEntry zipEntry = zipFile.getEntry(fileString);

        return zipFile.getInputStream(zipEntry);

    }

    /**
     * 解压一个压缩文档 到指定位置
     *
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
        java.util.zip.ZipEntry zipEntry;
        String szName = "";

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();

            } else {
                File file = new File(outPathString + File.separator + szName);
                //          Logger.e("[ZipUtils]" + outPathString + java.io.File.separator + szName);
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();

    }
    /**
     * 压缩文件,文件夹
     *
     * @param srcFileString 要压缩的文件/文件夹名字
     * @param zipFileString 指定压缩的目的和名字
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        android.util.Log.v("XZip", "ZipFolder(String, String)");

        //创建Zip包
        java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFileString));

        //打开要输出的文件
        File file = new File(srcFileString);

        //压缩
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);

        //完成,关闭
        outZip.finish();
        outZip.close();

    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, java.util.zip.ZipOutputStream zipOutputSteam) throws Exception {
        //    android.util.Log.v("XZip", "ZipFiles(String, String, ZipOutputStream)");

        if (zipOutputSteam == null)
            return;

        File file = new File(folderString + fileString);

        //判断是不是文件
        if (file.isFile()) {

            java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileString);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }

            zipOutputSteam.closeEntry();
        } else {

            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }//end of for

        }//end of if
    }


    /**
     *   确定该文件是否存在
     * @param path
     * @return
     */
    public static boolean checkExit(String path){
        File file = new File(getFilePath(path));
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }



    public static String getFilePath(String souceString){
        int i = souceString.lastIndexOf("/");
        return ConstantValues.DOWNLOAD + "/" +souceString.substring(i+1,souceString.length() );
    }



    private void saveBitmap(Bitmap bitmap,String bitName) throws IOException
    {
        File file = new File("/sdcard/DCIM/Camera/"+bitName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static List<String> getImage(String dirPath){
        List<String> list = new ArrayList<>();
        File  dir = new File(dirPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")) {
                    list.add(path);
                }
            }
        }
        return list;
    }

    /**
     * 获取文件编码
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String getCharset(String fileName) throws IOException{
        String charset;
        FileInputStream fis = new FileInputStream(fileName);
        byte[] buf = new byte[4096];
        // (1)
        UniversalDetector detector = new UniversalDetector(null);
        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();
        // (4)
        charset = detector.getDetectedCharset();
        // (5)
        detector.reset();
        return charset;
    }

    /**
     * 根据路径获取文件名
     * @param pathandname
     * @return
     */
    public static String getFileName(String pathandname){
        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return "";
        }

    }

    public static  List<File> getSuffixFile(String filePath, String suffere){
        List<File> files = new ArrayList<>();
        File f = new File(filePath);
        return getSuffixFile(files,f,suffere);
    }

    /**
     * 读取sd卡上指定后缀的所有文件
     * @param files 返回的所有文件
     * @param f 路径(可传入sd卡路径)
     * @param suffere 后缀名称 比如 .gif
     * @return
     */
    public static  List<File> getSuffixFile(List<File> files, File f, final String suffere) {
        if (!f.exists()) {
            return null;
        }

        File[] subFiles = f.listFiles();
        for (File subFile : subFiles) {
            if (subFile.isHidden()){
                continue;
            }
            if(subFile.isDirectory()){
                getSuffixFile(files, subFile, suffere);
            }else if(subFile.getName().endsWith(suffere)){
                files.add(subFile);
            }
        }
        return files;
    }



    public static void saveString(String data, String name,String filePath) {
        File sdCardDir = new File(filePath);
        if (!sdCardDir.exists()) {
            if (!sdCardDir.mkdirs()) {
                try {
                    sdCardDir.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String fileName = name + ".txt";

        File saveFile = new File(sdCardDir, fileName);
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            writeData(data, false, saveFile);
        } catch (Exception e) {
            Log.e("novelWrite"," write book :" + name +" have a error"+ e.toString());
        }

    }
    /**
     * @param content        写入内容
     * @param isClearContent 是否清楚原来内容 true 覆盖数据 false 累加内容
     */
    public static void writeData(String content, Boolean isClearContent, File saveFile) {
        try {
            if (isClearContent) {
                final FileOutputStream outStream = new FileOutputStream(saveFile);
                try {
                    //内容写入文件
                    outStream.write(content.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //内容追加

                BufferedWriter out = null;
                try {
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile, true)));
                    out.write(content + "\r\n");
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

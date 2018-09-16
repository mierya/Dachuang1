package jifei.dachuang.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
专门用来进行文件操作，大部分方法设为public static
*/
public class FileOperations
{
    /*public static File getCacheFile(String name) {
        String cachePath;
        Context context = getAppContext();
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + name);
    }*/
    public static void copyAssets(Context context,String dir)//将assets文件夹下所有内容复制到dir路径
    {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try
        {
            files = assetManager.list("");
        }
        catch (IOException e)
        {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files)
        {
            InputStream in = null;
            OutputStream out = null;
            try
            {
                in = assetManager.open(filename);
                File outFile = new File(dir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e)
            {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally
            {
                if (in != null)
                {
                    try
                    {
                        in.close();
                    } catch (IOException e)
                    {
                        // NOOP
                    }
                }
                if (out != null)
                {
                    try
                    {
                        out.close();
                    } catch (IOException e)
                    {
                        // NOOP
                    }
                }
            }
        }
    }
    private static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}

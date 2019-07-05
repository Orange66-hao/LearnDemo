package net.boocu.web.controller.common;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
 


import org.apache.commons.lang.StringUtils;

 

 
/**
 * File操作帮助类基于commons.io.FileUtils
 * @author Administrator
 *
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
        public static final String DIR_SEP = System.getProperty("file.separator");
        private final static int KILOBYTE = 1024;

        /**
         * Reads a text file.
         *  
         * @param fileName the name of the text file
         * @return the lines of the text file
         * @throws FileNotFoundException when the file was not found
         * @throws IOException when file could not be read.
         */
        public static String[] readTextFile(String fileName) throws FileNotFoundException, IOException {
                return readTextFile(new File(fileName));
        }

        /**
         * Changes the filename suffix.
         *
         * @param filename the filename to be changed
         * @param suffix the new suffix of the file
         * @return the filename with the replaced suffix
         */
        public static String changeFileNameSuffixTo(String filename, String suffix) {

                int dotPos = filename.lastIndexOf('.');
                if (dotPos != -1) {
                        return filename.substring(0, dotPos + 1) + suffix;
                } else {
                        // the string has no suffix
                        return filename;
                }
        }

        /**
         * 尝试删除文件(非目录)尝试3次
         * @param file
         */
        public static void removeFile(File file) {
                int maxTry = 3;
                while (maxTry > 0) {
                        maxTry--;
                        if (file.isFile()) {
                                if (file.delete())
                                        return;
                                else
                                        continue;
                        } else {
                                return;
                        }
                }
        }

        public static void writeFile(File file, byte[] data) throws IOException {
                final int MAX_BUFFER_SIZE = 4096;
                FileOutputStream output = null;
                FileChannel fc = null;
                try {
                        output = new FileOutputStream(file);
                        fc = output.getChannel();
                        ByteBuffer buffer = ByteBuffer.allocate(MAX_BUFFER_SIZE);
                        int offset = 0;
                        while (offset < data.length) {
                                buffer.clear();
                                int len = data.length - offset;
                                if (len > MAX_BUFFER_SIZE)
                                        len = MAX_BUFFER_SIZE;
                                buffer.put(data, offset, len);
                                offset += len;
                                buffer.flip();
                                fc.write(buffer);
                        }
                } finally {
                        if (fc != null) {
                                try {
                                        fc.close();
                                } catch (IOException e) {
                                }
                        }
                        if (output != null) {
                                try {
                                        output.close();
                                } catch (IOException e) {
                                }
                        }
                }
        }

        public static void readFile(File file, OutputStream output) throws IOException {
                FileInputStream input = null;
                FileChannel fc = null;
                try {
                        input = new FileInputStream(file);
                        fc = input.getChannel();
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        for (;;) {
                                buffer.clear();
                                int n = fc.read(buffer);
                                if (n == (-1))
                                        break;
                                output.write(buffer.array(), 0, buffer.position());
                        }
                } finally {
                        if (fc != null) {
                                try {
                                        fc.close();
                                } catch (IOException e) {
                                }
                        }
                        if (input != null) {
                                try {
                                        input.close();
                                } catch (IOException e) {
                                }
                        }
                }
        }

        /**
         * Returns the system-dependent version of the given file or path. This is
         * done by converting all the slash characters to the correct slash for the
         * current operating system
         *
         * @param   path The path to convert to the current operating system
         * @return  The converted path
         */
        public static String convertPath(String path) {

                String newPath;
                if (File.separatorChar == '\\') {
                        newPath = path.replace('/', '\\');
                } else {
                        newPath = path.replace('\\', '/');
                }

                return newPath;
        }

        /**
         * Reads a text file.
         *  
         * @param file the text file
         * @return the lines of the text file
         * @throws FileNotFoundException when the file was not found
         * @throws IOException when file could not be read.
         */
        public static String[] readTextFile(File file) throws FileNotFoundException, IOException {
                ArrayList lines = new ArrayList();
                BufferedReader in = new BufferedReader(new FileReader(file));
                String line;
                while ((line = in.readLine()) != null) {
                        lines.add(line);
                }
                in.close();
                return (String[]) lines.toArray(new String[lines.size()]);
        }

        /**
         * Reads a text file.
         *  
         * @param file the text file
         * @param encoding the encoding of the textfile
         * @return the lines of the text file
         * @throws FileNotFoundException when the file was not found
         * @throws IOException when file could not be read.
         */
        public static String[] readTextFile(File file, String encoding) throws FileNotFoundException, IOException {
                return readTextFile(new FileInputStream(file), encoding);
        }

        /**
         * Reads the text from the given input stream in the default encoding.
         * 
         * @param in the input stream
         * @return the text contained in the stream
         * @throws IOException when stream could not be read.
         */
        public static String[] readTextFile(InputStream in) throws IOException {
                return readTextFile(in, null);
        }

        /**
         * Reads the text from the given input stream in the default encoding.
         * 
         * @param in the input stream
         * @param encoding the encoding of the textfile
         * @return the text contained in the stream
         * @throws IOException when stream could not be read.
         */
        public static String[] readTextFile(InputStream in, String encoding) throws IOException {
                ArrayList lines = new ArrayList();
                BufferedReader bufferedIn;
                if (encoding != null) {
                        bufferedIn = new BufferedReader(new InputStreamReader(in, encoding));
                } else {
                        bufferedIn = new BufferedReader(new InputStreamReader(in));
                }
                String line;
                while ((line = bufferedIn.readLine()) != null) {
                        lines.add(line);
                }
                bufferedIn.close();
                in.close();
                return (String[]) lines.toArray(new String[lines.size()]);
        }

        /**
         * Writes (and creates) a text file.
         * 
         * @param file the file to which the text should be written
         * @param lines the text lines of the file in a collection with String-values
         * @throws IOException when there is an input/output error during the saving
         */
        public static void writeTextFile(File file, Collection lines) throws IOException {
                writeTextFile(file, (String[]) lines.toArray(new String[lines.size()]));
        }

        /**
         * Writes (and creates) a text file.
         * 
         * @param file the file to which the text should be written
         * @param lines the text lines of the file
         * @throws IOException when there is an input/output error during the saving
         */
        public static void writeTextFile(File file, String[] lines) throws IOException {
                File parentDir = file.getParentFile();
                if ((parentDir != null) && !parentDir.exists()) {
                        parentDir.mkdirs();
                }
                PrintWriter out = new PrintWriter(new FileWriter(file));
                for (int i = 0; i < lines.length; i++) {
                        out.println(lines[i]);
                }
                out.close();
        }

        public static long getFileOrDirectorySize(File file) {
                if (file.isFile()) {
                        return file.length();
                } else {
                        long res = 0;
                        File[] files = file.listFiles();
                        if (files != null) {
                                for (int i = 0; i < files.length; i++) {
                                        res += getFileOrDirectorySize(files[i]);
                                }
                        }
                        return res;
                }
        }

        public static String guessContentTypeFromFileType(String file_type) {
                String response_type = null;

                if (file_type == null) {

                        response_type = "application/octet-stream";

                } else {

                        if (file_type.equals("html") || file_type.equals("htm")) {
                                response_type = "text/html";
                        } else if (file_type.equals("css")) {
                                response_type = "text/css";
                        } else if (file_type.equals("xml")) {
                                response_type = "text/xml";
                        } else if (file_type.equals("xsl")) {
                                response_type = "text/xml";
                        } else if (file_type.equals("jpg") || file_type.equals("jpeg")) {
                                response_type = "image/jpeg";
                        } else if (file_type.equals("gif")) {
                                response_type = "image/gif";
                        } else if (file_type.equals("tiff")) {
                                response_type = "image/tiff";
                        } else if (file_type.equals("bmp")) {
                                response_type = "image/bmp";
                        } else if (file_type.equals("png")) {
                                response_type = "image/png";
                        } else if (file_type.equals("torrent") || file_type.equals("tor")) {
                                response_type = "application/x-bittorrent";
                        } else if (file_type.equals("zip")) {
                                response_type = "application/zip";
                        } else if (file_type.equals("txt")) {
                                response_type = "text/plain";
                        } else if (file_type.equals("jar")) {
                                response_type = "application/java-archive";
                        } else if (file_type.equals("jnlp")) {
                                response_type = "application/x-java-jnlp-file";
                        } else if (file_type.equals("mp3")) {
                                response_type = "audio/x-mpeg";
                        } else {
                                response_type = "application/octet-stream";
                        }
                }

                return (response_type);
        }

        public static double getSize(String filename) {
                if (!StringUtils.isEmpty(filename)) {
                        return (getSize(new File(filename)));
                } else {
                        return (0);
                }
        }

        public static double getSize(File file) {
                double size = 0;
                if (file != null) {
                        if (file.isFile()) {
                                size = (double) file.length() / (double) KILOBYTE;
                                return (size);
                        } else {
                                File[] subFiles = file.listFiles();
                                for (int i = 0; i < subFiles.length; i++) {
                                        if (subFiles[i].isFile()) {
                                                size += (double) subFiles[i].length() / (double) KILOBYTE;
                                        } else {
                                                size += getSize(subFiles[i]);
                                        }
                                }
                        }
                }
                return (size);
        }

        /**
         * 功能描述：拷贝一个目录或者文件到指定路径下，即把源文件拷贝到目标文件路径下
         * 
         * @param source
         *            源文件
         * @param target
         *            目标文件路径
         * @return void
         */
        public static void copy1(File source, File target) {
                File tarpath = new File(target, source.getName());
                if (source.isDirectory()) {
                        tarpath.mkdir();
                        File[] dir = source.listFiles();
                        for (int i = 0; i < dir.length; i++) {
                                copy1(dir[i], tarpath);
                        }
                } else {
                        try {
                                InputStream is = new FileInputStream(source); // 用于读取文件的原始字节流
                                OutputStream os = new FileOutputStream(tarpath); // 用于写入文件的原始字节的流
                                byte[] buf = new byte[1024];// 存储读取数据的缓冲区大小
                                int len = 0;
                                while ((len = is.read(buf)) != -1) {
                                        os.write(buf, 0, len);
                                }
                                is.close();
                                os.close();
                        } catch (FileNotFoundException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }

      
        /**以文件流的方式复制文件 
         * 该方法经过测试，支持中文处理，并且可以复制多种类型，比如txt，xml，jpg，doc等多种格式 
        * @param src 文件源目录 
        * @param dest 文件目的目录 
        * @throws IOException 
        */
        public void copyFileByStream(String src, String dest) throws IOException {
                FileInputStream in = new FileInputStream(src);
                File file = new File(dest);
                if (!file.exists())
                        file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                int c;
                byte buffer[] = new byte[1024];
                while ((c = in.read(buffer)) != -1) {
                        for (int i = 0; i < c; i++)
                                out.write(buffer[i]);
                }
                in.close();
                out.close();
        }

        /**
         * 删除目录
         * @param directory
         */
        public static void deltree(String directory) {
                deltree(new File(directory));
        }

        /**
         * 删除目录
         * @param directory
         */
        public static void deltree(File directory) {
                if (directory.exists() && directory.isDirectory()) {
                        File[] fileArray = directory.listFiles();

                        for (int i = 0; i < fileArray.length; i++) {
                                if (fileArray[i].isDirectory()) {
                                        deltree(fileArray[i]);
                                } else {
                                        fileArray[i].delete();
                                }
                        }

                        directory.delete();
                }
        }

        public static String getPath(String fullFileName) {
                int pos = fullFileName.lastIndexOf("/");

                if (pos == -1) {
                        pos = fullFileName.lastIndexOf("\\");
                }

                String shortFileName = fullFileName.substring(0, pos);

                if (StringUtils.isBlank(shortFileName)) {
                        return "/";
                }

                return shortFileName;
        }

        public static File[] sortFiles(File[] files) {
                Arrays.sort(files, new FileDateComparator());

                List<File> directoryList = new ArrayList<File>();
                List<File> fileList = new ArrayList<File>();

                for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                                directoryList.add(files[i]);
                        } else {
                                fileList.add(files[i]);
                        }
                }

                directoryList.addAll(fileList);

                return directoryList.toArray(new File[0]);
        }

        public static void copyFileByFileChannel(File source, File destination) {
                if (!source.exists()) {
                        return;
                }

                if ((destination.getParentFile() != null) && (!destination.getParentFile().exists())) {

                        destination.getParentFile().mkdirs();
                }

                try {
                        FileChannel srcChannel = new FileInputStream(source).getChannel();
                        FileChannel dstChannel = new FileOutputStream(destination).getChannel();

                        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

                        srcChannel.close();
                        dstChannel.close();
                } catch (IOException ioe) {
                        ioe.printStackTrace();
                }
        }

        public static void copyByNIO(File source, File target) throws IOException {
                FileChannel input = null;
                FileChannel output = null;

                try {
                        input = new FileInputStream(source).getChannel();
                        output = new FileOutputStream(target).getChannel();

                        MappedByteBuffer buffer = input.map(FileChannel.MapMode.READ_ONLY, 0, input.size());
                        output.write(buffer);
                } finally {
                        if (input != null) {
                                input.close();
                        }

                        if (output != null) {
                                output.close();
                        }
                }
        }

        public static void copy(File source, File target) throws Exception {
                if (null == source)
                        throw new IllegalArgumentException("source can't be null.");
                if (null == target)
                        throw new IllegalArgumentException("target can't be null.");

                try {
                        FileInputStream file_input_stream = new FileInputStream(source);
                        FileOutputStream file_output_stream = new FileOutputStream(target);

                        copy(file_input_stream, file_output_stream);

                        file_output_stream.close();
                        file_input_stream.close();
                } catch (IOException e) {
                        throw new IOException("Error while copying file '" + source.getAbsolutePath() + "' to file '"
                                        + target.getAbsolutePath() + "'.", e);
                }
        }

        /**
         * Creates a temporary file, using the class name to construct the prefix.  Suffix
         * is the default.  The file is automatically set to delete on exit.
         * @param c Class requesting temporary file
         * @return Temporary file object, with deleteOnExit set
         * @throws IOException Could not create file
         * @see java.io.File#createTempFile(java.lang.String, java.lang.String)
         */
        public static File createTempFile(Class c) throws IOException {
                return deleteTempFileOnExit(File.createTempFile("TMP_" + c.getSimpleName(), null));
        }

        /**
         * Sets the file to delete on exit and returns the file.
         * @param f File to set
         * @return Same file object
         * @see java.io.File#deleteOnExit()
         */
        private static File deleteTempFileOnExit(File f) {
                f.deleteOnExit();
                return f;
        }

       

  
 

        public static String getExtension(File file) {
                return getExtension(file.getName());
        }

        public static String readString(File source, String encoding) throws IOException {
                if (null == source)
                        throw new IllegalArgumentException("source can't be null.");

                try {
                        FileInputStream file_input_stream = new FileInputStream(source);
                        String content = null;
                        if (null == encoding) {
                                content = readString(file_input_stream);
                        } else {
                                content = readString(file_input_stream, encoding);
                        }
                        file_input_stream.close();
                        return content;
                } catch (IOException e) {
                        throw new IOException("Error while reading url '" + source.getAbsolutePath() + ".", e);
                }
        }

        public static void writeString(String content, File destination) throws IOException {
                if (null == content)
                        throw new IllegalArgumentException("content can't be null.");
                if (null == destination)
                        throw new IllegalArgumentException("destination can't be null.");

                try {
                        FileWriter file_writer = null;

                        file_writer = new FileWriter(destination);

                        file_writer.write(content, 0, content.length());
                        file_writer.flush();
                        file_writer.close();
                } catch (IOException e) {
                        throw new IOException("Error while write a string to '" + destination.getAbsolutePath() + ".", e);
                }
        }

        /**得到文件的扩展名.   
         *  @param fileName    需要处理的文件的名字.     
         *  @return the extension portion of the file's name.     */
        public static String getExtension(String fileName) {
                if (null == fileName)
                        throw new IllegalArgumentException("fileName can't be null.");

                String ext = null;

                int index = fileName.lastIndexOf('.');
                if (index > 0 && index < fileName.length() - 1) {
                        ext = fileName.substring(index + 1).toLowerCase();
                }

                return ext;
        }

        public static String getBaseName(File file) {
                return getBaseName(file.getName());
        }

        public static void deleteFile(File file) {
                if (null == file)
                        throw new IllegalArgumentException("file can't be null.");

                file.delete();
        }

        public static String getBaseName(String fileName) {
                if (null == fileName)
                        throw new IllegalArgumentException("fileName can't be null.");

                String basename = null;

                int index = fileName.lastIndexOf('.');
                if (index > 0 && index < fileName.length() - 1) {
                        basename = fileName.substring(0, index);
                }

                return basename;
        }

        public static void copy(InputStream inputStream, OutputStream outputStream) throws Exception {
                if (null == inputStream)
                        throw new IllegalArgumentException("inputStream can't be null.");
                if (null == outputStream)
                        throw new IllegalArgumentException("outputStream can't be null.");

                try {
                        byte[] buffer = new byte[1024];
                        int return_value = -1;

                        return_value = inputStream.read(buffer);

                        while (-1 != return_value) {
                                outputStream.write(buffer, 0, return_value);
                                return_value = inputStream.read(buffer);
                        }
                } catch (IOException e) {
                        throw new IOException("Error during the copying of streams.", e);
                }
        }

        public static void copy(InputStream inputStream, File target) throws Exception {
                if (null == inputStream)
                        throw new IllegalArgumentException("inputStream can't be null.");
                if (null == target)
                        throw new IllegalArgumentException("target can't be null.");

                try {
                        FileOutputStream file_output_stream = new FileOutputStream(target);

                        copy(inputStream, file_output_stream);

                        file_output_stream.close();
                } catch (IOException e) {
                        throw new IOException("Error while copying an input stream to file '" + target.getAbsolutePath() + "'.", e);
                }
        }

        public static void copy(File source, OutputStream outputStream) throws Exception {
                if (null == source)
                        throw new IllegalArgumentException("source can't be null.");
                if (null == outputStream)
                        throw new IllegalArgumentException("outputStream can't be null.");

                try {
                        FileInputStream file_input_stream = new FileInputStream(source);

                        copy(file_input_stream, outputStream);

                        file_input_stream.close();
                } catch (IOException e) {
                        throw new IOException("Error while copying file '" + source.getAbsolutePath() + "' to an output stream.", e);
                }
        }

        public static ByteArrayOutputStream readStream(InputStream inputStream) throws IOException {
                if (null == inputStream)
                        throw new IllegalArgumentException("inputStream can't be null.");

                try {
                        byte[] buffer = new byte[1024];
                        int return_value = -1;
                        ByteArrayOutputStream output_stream = new ByteArrayOutputStream(buffer.length);

                        return_value = inputStream.read(buffer);

                        while (-1 != return_value) {
                                output_stream.write(buffer, 0, return_value);
                                return_value = inputStream.read(buffer);
                        }

                        output_stream.close();

                        inputStream.close();

                        return output_stream;
                } catch (IOException e) {
                        throw new IOException("Error while reading the complete contents of an input stream.", e);
                }
        }

        public static String readString(InputStream inputStream) throws IOException {
                if (null == inputStream)
                        throw new IllegalArgumentException("inputStream can't be null.");

                return readStream(inputStream).toString();
        }

        public static String readString(Reader reader) throws IOException {
                if (null == reader)
                        throw new IllegalArgumentException("reader can't be null.");

                try {
                        char[] buffer = new char[1024];
                        StringBuilder result = new StringBuilder();

                        int size = reader.read(buffer);
                        while (size != -1) {
                                result.append(buffer, 0, size);
                                size = reader.read(buffer);
                        }

                        return result.toString();
                } catch (IOException e) {
                        throw new IOException("Error while reading the complete contents of an reader.", e);
                }
        }

        public static String readString(InputStream inputStream, String encoding) throws IOException {
                if (null == inputStream)
                        throw new IllegalArgumentException("inputStream can't be null.");

                try {
                        return readStream(inputStream).toString(encoding);
                } catch (UnsupportedEncodingException e) {
                        throw new IOException("Encoding '" + encoding + "' is not supported.", e);
                }
        }

        public static void deleteAll(File[] files) {
                for (File file : files) {
                        if (file.isDirectory()) {
                                deleteAll(file.listFiles());
                        }

                        file.delete();
                }
        }

        public static abstract class FileComparator implements Comparator<File> {
                private boolean ascending = true;

                public void setAscending(boolean ascending) {
                        this.ascending = ascending;
                }

                public boolean isAscending() {
                        return (this.ascending);
                }

                
				public int compare(File file1, File file2) {
                        return (this.ascending ? this.compare(file1, file2) : -this.compare(file1, file2));
                }
        }

        private static class FileDateComparator extends FileComparator {
                
				public int compare(File file1, File file2) {
                        return (new Date(file1.lastModified()).compareTo(new Date(file2.lastModified())));
                }
        }

        private static class FileNameComparator extends FileComparator {
                
				public int compare(File file1, File file2) {
                        return file1.getName().compareTo(file2.getName());
                }
        }

        public static List<String> fromFile(String fileName) throws IOException {
                return fromFile(new File(fileName));
        }

        public static List<String> fromFile(File file) throws IOException {
                List<String> list = new ArrayList<String>();

                BufferedReader br = new BufferedReader(new FileReader(file));

                String s = "";

                while ((s = br.readLine()) != null) {
                        list.add(s);
                }

                br.close();

                return list;
        }

        public static long getFileSize(File file) {
                if (!file.exists()) {
                        return -1L;
                }
                long size = 0;
                if (file.isDirectory()) {
                        File[] files = file.listFiles();
                        if (files != null && files.length > 0) {
                                for (File f : files) {
                                        size += getFileSize(f);
                                }
                        }
                } else {
                        size += file.length();
                }
                return size;
        }

        /**
         * Finds files within a given directory (and optionally its
         * subdirectories). All files found are filtered by an IOFileFilter.
         */
        public static List<File> listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
                //if(!directory.isDirectory()) {
                //    throw new IllegalArgumentException("Parameter 'directory' is not a directory");
                //}
                if (fileFilter == null) {
                        throw new NullPointerException("Parameter 'fileFilter' is null");
                }
                //Setup effective file filter
                IOFileFilter effFileFilter = new AndFileFilter(fileFilter, new NotFileFilter(DirectoryFileFilter.INSTANCE));
                //Setup effective directory filter
                final IOFileFilter effDirFilter;
                if (dirFilter == null) {
                        effDirFilter = FalseFileFilter.INSTANCE;
                } else {
                        effDirFilter = new AndFileFilter(dirFilter, DirectoryFileFilter.INSTANCE);
                }
                //Find files
                List<File> files = new ArrayList<File>(12);
                innerListFiles(files, directory, new OrFileFilter(effFileFilter, effDirFilter));
                return files;
        }

        /**
         * Finds files within a given directory (and optionally its
         * subdirectories). All files found are filtered by an IOFileFilter.
         */
        private static void innerListFiles(Collection<File> files, File directory, IOFileFilter filter) {
                File[] found = directory.listFiles((FileFilter) filter);
                if (found != null) {
                        for (int i = 0; i < found.length; i++) {
                                if (found[i].isDirectory()) {
                                        innerListFiles(files, found[i], filter);
                                } else {
                                        files.add(found[i]);
                                }
                        }
                }
        }

        public static List<File> listFiles(File directory, String suffix, boolean recursive) {
                return listFiles(directory, new String[] { suffix }, recursive);
        }

        /**
         * Finds files within a given directory (and optionally its subdirectories)
         * which match an array of suffixes.
         */
        public static List<File> listFiles(File directory, String[] suffixes, boolean recursive) {
                final IOFileFilter filter;
                if (suffixes == null) {
                        filter = TrueFileFilter.INSTANCE;
                } else {
                        filter = new SuffixFileFilter(suffixes);
                }
                return listFiles(directory, filter, (recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
        }

        public static List<File> listFiles(File directory, String[] prefixes, String[] suffixes, boolean recursive) {
                IOFileFilter fileFiler = null;
                if (prefixes != null && prefixes.length > 0) {
                        fileFiler = new PrefixFileFilter(prefixes);
                }
                if (suffixes != null && suffixes.length > 0) {
                        fileFiler = new AndFileFilter(fileFiler, new SuffixFileFilter(suffixes));
                }
                return listFiles(directory, (fileFiler == null ? TrueFileFilter.INSTANCE : fileFiler),
                                (recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE));
        }

        public static String getFileName(File file) {
                assert (file != null);
                if (!file.exists()) {
                        return null;
                }
                String filepath = file.getName();
                int i = filepath.lastIndexOf(File.separator);
                return (i >= 0) ? filepath.substring(i + 1) : filepath;
        }

        public static String basename(String filepath) {
                final int index = filepath.lastIndexOf(File.separatorChar);
                if (-1 == index) {
                        return filepath;
                } else {
                        return filepath.substring(index + 1);
                }
        }

        public static String basename(String filepath, char separator) {
                final int index = filepath.lastIndexOf(separator);
                if (-1 == index) {
                        return filepath;
                } else {
                        return filepath.substring(index + 1);
                }
        }

        public static String dirName(String filepath, char separatorChar) {
                final int index = filepath.lastIndexOf(separatorChar);
                if (-1 == index) {
                        return new String(new char[] { separatorChar });
                } else {
                        return filepath.substring(0, index);
                }
        }

        public static void truncateFile(File file) {
                final RandomAccessFile raf;
                try {
                        raf = new RandomAccessFile(file, "rw");
                } catch (FileNotFoundException fnfe) {
                        throw new IllegalStateException(fnfe);
                }
                try {
                        raf.setLength(0);
                } catch (IOException ioe) {
                        throw new IllegalStateException(ioe);
                } finally {
                	try {
						raf.close();
					} catch (IOException e) {
						 
					}
                }
        }
           public static File toFile(URL url) {
                if (!url.getProtocol().equals("file")) {
                    return null;
                } else {
                    String filename = url.getFile().replace('/', File.separatorChar);
                    return new File(filename);
                }
            }

        public interface IOFileFilter extends FileFilter, FilenameFilter {
        }

        public static final class TrueFileFilter implements IOFileFilter {
                static final TrueFileFilter INSTANCE = new TrueFileFilter();

                private TrueFileFilter() {
                }

                
				public boolean accept(File pathname) {
                        return true;
                }

                
				public boolean accept(File dir, String name) {
                        return true;
                }
        }

        public static final class FalseFileFilter implements IOFileFilter {
                static final FalseFileFilter INSTANCE = new FalseFileFilter();

                private FalseFileFilter() {
                }

                
				public boolean accept(File pathname) {
                        return false;
                }

                
				public boolean accept(File dir, String name) {
                        return false;
                }
        }

        public static final class PrefixFileFilter implements IOFileFilter {
                private final String[] prefixes;

                public PrefixFileFilter(String... prefixes) {
                        if (prefixes == null) {
                                throw new IllegalArgumentException("The array of prefixes must not be null");
                        }
                        this.prefixes = prefixes;
                }

                
				public boolean accept(File file) {
                        String name = file.getName();
                        for (int i = 0; i < this.prefixes.length; i++) {
                                if (name.startsWith(this.prefixes[i])) {
                                        return true;
                                }
                        }
                        return false;
                }

                
				public boolean accept(File file, String name) {
                        for (int i = 0; i < prefixes.length; i++) {
                                if (name.startsWith(prefixes[i])) {
                                        return true;
                                }
                        }
                        return false;
                }
        }

        public static final class SuffixFileFilter implements IOFileFilter {
                private final String[] suffixes;

                public SuffixFileFilter(String... suffixes) {
                        if (suffixes == null) {
                                throw new IllegalArgumentException("The array of suffixes must not be null");
                        }
                        this.suffixes = suffixes;
                }

                
				public boolean accept(File file) {
                        String name = file.getName();
                        for (int i = 0; i < this.suffixes.length; i++) {
                                if (name.endsWith(this.suffixes[i])) {
                                        return true;
                                }
                        }
                        return false;
                }

                
				public boolean accept(File file, String name) {
                        for (int i = 0; i < this.suffixes.length; i++) {
                                if (name.endsWith(this.suffixes[i])) {
                                        return true;
                                }
                        }
                        return false;
                }
        }

        public static final class AndFileFilter implements IOFileFilter {
                private final IOFileFilter[] fileFilters;

                public AndFileFilter(IOFileFilter... filter) {
                        assert (filter != null);
                        this.fileFilters = filter;
                }

                
				public boolean accept(final File file) {
                        if (this.fileFilters.length == 0) {
                                return false;
                        }
                        for (IOFileFilter fileFilter : fileFilters) {
                                if (!fileFilter.accept(file)) {
                                        return false;
                                }
                        }
                        return true;
                }

                
				public boolean accept(final File file, final String name) {
                        if (this.fileFilters.length == 0) {
                                return false;
                        }
                        for (IOFileFilter fileFilter : fileFilters) {
                                if (!fileFilter.accept(file, name)) {
                                        return false;
                                }
                        }
                        return true;
                }
        }

        public static final class OrFileFilter implements IOFileFilter {
                private final IOFileFilter[] fileFilters;

                public OrFileFilter(IOFileFilter... filter) {
                        assert (filter != null);
                        this.fileFilters = filter;
                }

                
				public boolean accept(final File file) {
                        for (IOFileFilter fileFilter : fileFilters) {
                                if (fileFilter.accept(file)) {
                                        return true;
                                }
                        }
                        return false;
                }

                
				public boolean accept(final File file, final String name) {
                        for (IOFileFilter fileFilter : fileFilters) {
                                if (fileFilter.accept(file, name)) {
                                        return true;
                                }
                        }
                        return false;
                }
        }

        public static final class NotFileFilter implements IOFileFilter {
                private final IOFileFilter filter;

                public NotFileFilter(IOFileFilter filter) {
                        if (filter == null) {
                                throw new IllegalArgumentException("The filter must not be null");
                        }
                        this.filter = filter;
                }

                
				public boolean accept(File file) {
                        return !filter.accept(file);
                }

                
				public boolean accept(File file, String name) {
                        return !filter.accept(file, name);
                }
        }

        public static final class DirectoryFileFilter implements IOFileFilter {
                public static final DirectoryFileFilter INSTANCE = new DirectoryFileFilter();

                private DirectoryFileFilter() {
                }

                
				public boolean accept(File file) {
                        return file.isDirectory();
                }

                
				public boolean accept(File dir, String name) {
                        return accept(new File(dir, name));
                }
        }

        public static final class NameFileFilter implements IOFileFilter {
                private final String[] names;

                public NameFileFilter(String... names) {
                        if (names == null) {
                                throw new IllegalArgumentException("The array of names must not be null");
                        }
                        this.names = names;
                }

                
				public boolean accept(File file) {
                        String name = file.getName();
                        for (int i = 0; i < this.names.length; i++) {
                                if (name.equals(this.names[i])) {
                                        return true;
                                }
                        }
                        return false;
                }

                
				public boolean accept(File file, String name) {
                        for (int i = 0; i < this.names.length; i++) {
                                if (name.equals(this.names[i])) {
                                        return true;
                                }
                        }
                        return false;
                }
        }
         
    	/**
    	 * 获得文件扩展名
    	 * 
    	 * @param fileName
    	 * @return 不带"."的扩展名,无扩展名则返回""
    	 */
    	public static String getExName(String fileName) {
    		if (fileName == null || fileName.indexOf(".") == -1) {
    			return "";
    		}
    		return fileName.substring(fileName.lastIndexOf(".") + 1);
    	}

    	public static void delFile(String filePath) {
    		File file = new File(filePath);
    		if (file.exists())
    			file.delete();
    	}

        /**
         * @param args
         */
        public static void main(String[] args) {
                // TODO Auto-generated method stub
                //FilenameUtils
        }
        
        /**
         * 判断目标目录是否存在，如果不存在，就创建该目录
         * @param basePath 根目录，一定存在的目录
         * @param newFolder 子目录，可以是多个层次的，以逗号分开
         */
        public static void createPath(String basePath,String newFolder){
      	  String []floders=newFolder.split(",");
      	  String tempFloder=basePath;
      	  if(basePath.endsWith(File.separator))
      	  {
      		  tempFloder=tempFloder.substring(0, tempFloder.length()-2);
      	  }
      	  for(int i=0;i<floders.length;i++)
      	  {
      		  tempFloder=tempFloder+File.separator+floders[i];
      		  checkPath(tempFloder);
      	  }	  
           }

        /**
    	 * 判断目标目录是否存在，如果不存在，就创建该目录
    	 * @param filePath
    	 */
      public static void checkPath(String filePath){
    	  File folder = new File(filePath);
    	  try{
    	  if (!folder.exists()) {
    		  folder.mkdir();
    		  }}catch(Exception e){
    			e.printStackTrace();
              }	  
              }
}
package com.boom.android.util

import java.io.*

class FileAssistant {
    companion object{
        fun exists(path:String):Boolean{
            var file = File(path)
            return file.exists()
        }

        fun getFile(path: String):File{
            return File(path)
        }

        fun getFile(parent:File, child:String):File{
            return File(parent,child)
        }

        fun getName(path:String):String{
            return File(path).name
        }

        fun getLength(path:String):Long{
            val file = File(path)
            return if(file.exists()) file.length() else 0L
        }

        fun deleteFile(path: String):Boolean{
            val file = File(path)
            return if(file.exists()) file.delete() else false
        }

        fun deleteFile(parent:File?, child:String?):Boolean{
            if(parent==null || child==null){
                return false
            }
            val file = File(parent,child)
            return if(file.exists()) file.delete() else false
        }

        fun deleteFiles(parent:File):Boolean{
            if(parent==null || !parent.isDirectory){
                return false
            }
            val files = parent.listFiles()
            if(files==null || files.isEmpty()){
                return false
            }
            files.forEach { it.delete() }
            return true
        }

        fun deleteDir(dir:File):Boolean{
            return if(dir.isDirectory) dir.delete() else false
        }

        fun mkdirForNotExist(dir:File){
            if(!dir.exists()){
                dir.mkdir()
            }
        }

        fun mkdirs(parent:File, child:String): Boolean{
            val file = File(parent,child)
            return file.mkdirs()
        }

        @Throws(IOException::class)
        fun createForNoExist(file:File){
            try {
                if(!file.exists()){
                    file.createNewFile()
                }
            }catch (e: IOException){
                throw e
            }
        }

        fun getAbsolutePath(path:String):String?{
            var file = File(path)
            return file.absolutePath
        }

        fun getAbsolutePath(parent: File, child:String):String?{
            var file = File(parent,child)
            return file.absolutePath
        }

        fun countFiles(folder:File):Int{
            if(!folder.exists() || !folder.isDirectory){
                return -1
            }
            return if(folder.listFiles()!=null) folder.listFiles().size else -1
        }

        fun countFiles(path:String, filter: FilenameFilter):Int{
            var folder = File(path)
            if(!folder.exists() || !folder.isDirectory){
                return -1
            }
            return if(folder.listFiles(filter)!=null) folder.listFiles(filter).size else -1
        }

        @Throws(IOException::class)
        fun getOutputStreamAfterDelete(path:String):FileOutputStream{
            try{
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
                return FileOutputStream(file)
            }catch (e:IOException){
                throw e
            }
        }

        fun deleteDirIfEmpty(dir: File) : Boolean{
            if (!dir.isDirectory || !dir.exists()) {
                return true
            }
            return when(countFiles(dir) <= 0) {
                true -> {
                    deleteDir(dir)
                    true
                }
                else -> false
            }
        }
    }
}
package nathanmerrell.com.moneymanagement.databaseManagement

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import java.io.File
import java.io.IOException
import java.io.OutputStream

class DatabaseModule {
private lateinit var dbFile: File
    fun DBModule(context: Context, loadDB: Boolean){
        SQLiteDatabase.loadLibs(context)
        dbFile = copyDataBase(context, "MM.sqlite", loadDB)
    }
    fun databaseFile(): File{
        return dbFile
    }
    private fun copyDataBase(context: Context, dbName: String, loadDB: Boolean): File
    {
        var outFileName = File("")
        try {
            val mInput = context.assets.open(dbName)
            var files = context.fileList ()
            val exists = files.any { it == dbName }

            if (!exists || loadDB) {
                val mOutput: OutputStream = context.openFileOutput(dbName, Context.MODE_PRIVATE)
                outFileName = context.getFileStreamPath(dbName)

                var mBuffer = ByteArray(1024)
                var mLength = 0

                while ({mLength = mInput.read(mBuffer); mLength}() > 0)
                    mOutput.write(mBuffer, 0, mLength)

                mOutput.close()
                mInput.close()
            } else {
                outFileName = File (context.filesDir, dbName)
            }

        } catch (e: IOException) {
            println(e.message.toString())
        }
        return outFileName
    }
}
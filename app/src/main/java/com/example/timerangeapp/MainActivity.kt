package com.example.timerangeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timerangeapp.adapter.ResultAdapter
import com.example.timerange.data.AppDatabase
import com.example.timerange.data.ResultEntry
import com.example.timerange.data.ResultEntryDao

class MainActivity : AppCompatActivity() {

    private lateinit var editTextStart: EditText
    private lateinit var editTextEnd: EditText
    private lateinit var editTextCheck: EditText
    private lateinit var buttonCheck: Button
    private lateinit var buttonSave: Button
    private lateinit var textViewResult: TextView
    private lateinit var recyclerViewResults: RecyclerView
    private lateinit var resultAdapter: ResultAdapter

    private lateinit var db: AppDatabase
    private lateinit var resultEntryDao: ResultEntryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // レイアウトをセット
        setContentView(R.layout.activity_main)

        // ビューのバインド
        editTextStart = findViewById(R.id.editTextStart)
        editTextEnd = findViewById(R.id.editTextEnd)
        editTextCheck = findViewById(R.id.editTextCheck)
        buttonCheck = findViewById(R.id.buttonCheck)
        buttonSave = findViewById(R.id.buttonSave)
        textViewResult = findViewById(R.id.textViewResult)
        recyclerViewResults = findViewById(R.id.recyclerViewResults)

        // RecyclerView の設定
        recyclerViewResults.layoutManager = LinearLayoutManager(this)
        resultAdapter = ResultAdapter(emptyList())
        recyclerViewResults.adapter = resultAdapter

        // Room データベースの初期化
        db = AppDatabase.getInstance(this)
        resultEntryDao = db.resultEntryDao()

        // 保存済み結果の読み込み
        loadResults()

        // 判定ボタン押下時の処理
        buttonCheck.setOnClickListener {
            performCheck()
        }

        // 保存ボタン押下時の処理
        buttonSave.setOnClickListener {
            saveResult()
        }
    }

    /**
     * 入力された時刻情報から、ある時刻が指定の範囲に含まれるかを判定する
     */
    private fun performCheck() {
        val startText = editTextStart.text.toString()
        val endText = editTextEnd.text.toString()
        val checkText = editTextCheck.text.toString()

        if (startText.isEmpty() || endText.isEmpty() || checkText.isEmpty()) {
            Toast.makeText(this, "全ての時刻を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val startTime = startText.toIntOrNull()
        val endTime = endText.toIntOrNull()
        val checkTime = checkText.toIntOrNull()

        if (startTime == null || endTime == null || checkTime == null) {
            Toast.makeText(this, "正しい数値を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // 0～23 の範囲内かチェック
        if (startTime !in 0..23 || endTime !in 0..23 || checkTime !in 0..23) {
            Toast.makeText(this, "時刻は0から23の間で入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val isIncluded = isTimeInRange(checkTime, startTime, endTime)
        textViewResult.text = if (isIncluded) "含む" else "含まない"
    }

    /**
     * 時刻の判定ロジック
     * ・開始時刻と終了時刻が同じなら全て含むと判断（※もしくは「指定の時刻と等しい場合のみ」といった仕様も考えられますが、ここでは全時刻対象としています）
     * ・開始 < 終了 → 単純に [開始, 終了) の範囲でチェック
     * ・開始 > 終了 → 深夜を跨ぐため、[開始, 24) または [0, 終了) の範囲でチェック
     */
    private fun isTimeInRange(checkTime: Int, startTime: Int, endTime: Int): Boolean {
        return if (startTime == endTime) {
            true
        } else if (startTime < endTime) {
            checkTime in startTime until endTime
        } else {
            // 例：22時～5時の場合
            checkTime >= startTime || checkTime < endTime
        }
    }

    /**
     * 現在の入力内容の判定結果をデータベースに保存する
     */
    private fun saveResult() {
        val startText = editTextStart.text.toString()
        val endText = editTextEnd.text.toString()
        val checkText = editTextCheck.text.toString()

        if (startText.isEmpty() || endText.isEmpty() || checkText.isEmpty()) {
            Toast.makeText(this, "全ての時刻を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val startTime = startText.toIntOrNull()
        val endTime = endText.toIntOrNull()
        val checkTime = checkText.toIntOrNull()

        if (startTime == null || endTime == null || checkTime == null) {
            Toast.makeText(this, "正しい数値を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        if (startTime !in 0..23 || endTime !in 0..23 || checkTime !in 0..23) {
            Toast.makeText(this, "時刻は0から23の間で入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val isIncluded = isTimeInRange(checkTime, startTime, endTime)
        val resultEntry = ResultEntry(
            startTime = startTime,
            endTime = endTime,
            checkTime = checkTime,
            isIncluded = isIncluded
        )
        resultEntryDao.insert(resultEntry)
        Toast.makeText(this, "保存しました", Toast.LENGTH_SHORT).show()
        loadResults()
    }

    /**
     * データベースから結果を取得し、RecyclerView を更新する
     */
    private fun loadResults() {
        val results = resultEntryDao.getAll()
        resultAdapter.updateResults(results)
    }
}
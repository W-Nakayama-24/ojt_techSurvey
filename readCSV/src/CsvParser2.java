package readCSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * [OJT-要素技術調査課題] CSVファイル内のデータを読み込み、呼び出し元が利用できる形へ加工するクラス.
 *
 * @author W-nakayama
 */

public class CsvParser2 {

    // メンバ変数： 1レコードの途中に改行コードが入っているとき、以下の情報を保持したまま次の行に対して解析(parseメソッド)を実行するために宣言.
    int doubleQuotationCount = 0; // 1レコードに登場するダブルクォーテーションを数える. データそのものか、データの区切りかを判別する際に使用
    boolean isRecordEnd = true; // 改行によってレコードが途切れているのか、真にレコードが終わっているかを判別する際に使用
    // OSにより使用しているものが異なる改行コードをgetPropertyメソッドで用意
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * CSVファイルを読み込み、カンマで区切ったデータを二次元配列に格納して返却する.
     *
     * @param path パスをString型で引数に渡すことで,読み込むCSVファイルを指定する.
     * @return data2dArray カンマで区切ったCSVファイルのデータを二次元配列に格納して返却する.
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    public String[][] convertTo2dArray(String path) throws IOException, IrregularColumnsException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            // 1レコードあたりの"カンマで区切られたデータの数"を保持する
            int numElements = 0;

            // 解析した1行分のデータを一時的に収めるバッファを宣言 parseメソッドで使用
            StringBuffer stringBuffer = new StringBuffer();

            // 1レコードに存在しているデータをカンマで分割し、格納するリストを宣言
            List<String> dataList = new ArrayList<String>();

            // 1レコード分のデータを一次元配列に変換した後,リストに格納する
            List<String[]> recordList = new ArrayList<String[]>();

            // 読み込み,解析,コレクションへの格納を終端まで1レコードずつ実施する.
            String readLineResult;
            while ((readLineResult = bufferedReader.readLine()) != null) {

                // 解析の処理をまとめたparseLineメソッドを呼び出す 引数で読込結果、Stringバッファ、1レコードのデータを格納するリストを渡す
                parseLine(readLineResult, stringBuffer, dataList);

                // 1レコード分すべてのデータの解析が完了した場合
                // 1レコード分のデータが入ったリストを一次元配列に変換した後,一次元配列をコレクションするrecordListへ格納する
                if (isRecordEnd) {
                    String[] parsedData = dataList.toArray(new String[dataList.size()]);
                    recordList.add(parsedData);

                    numElements = dataList.size();
                    dataList.clear();
                }

            }

            // 読み込んだCSVファイルの1レコードあたりの要素数が揃っているかどうかをチェックする処理
            // recordListに入っているすべての一次元配列のlengthを順番に比較する
            int previousNumberOfData = 0;
            boolean isFirst = true;
            for (int i = 0; i < recordList.size(); i++) {
                if (isFirst) {
                    isFirst = false;
                    previousNumberOfData = recordList.get(i).length;
                } else {
                    if (previousNumberOfData != recordList.get(i).length) {
                        throw new IrregularColumnsException("CSVファイルの1レコードあたりの要素数が揃っていません");
                    }
                }
            }

            // [recordListのサイズ]×[1レコードあたりの要素数(numElements)]で返却用の二次元配列を宣言
            String[][] data2dArray = new String[recordList.size()][numElements];
            for (int i = 0; i < recordList.size(); i++) {
                // data2dArrayへの格納に用いる配列. 1レコード分のデータが順に格納された一次元配列をrecordListから取得し,格納する
                String[] elements = recordList.get(i);
                for (int j = 0; j < elements.length; j++) {
                    data2dArray[i][j] = elements[j];
                }
            }

            return data2dArray;
        }
    }

    /**
     * 親メソッド(convertTo2dArray)が読み込んで渡してきた1行分のデータを先頭から1文字ずつ参照し,
     * それぞれ「データそのもの」か「データの区切り」かを解析する. 解析後,データ毎に分けてリスト(dataList)へ格納する.
     *
     * @param readLineResult 親メソッド(convertTo2dArray)がCSVファイルの1行を読み込んだときの結果
     * @param stringBuffer   条件分岐の結果,データそのものであると見なした文字だけを格納するバッファ
     * @param dataList       カンマによって区切られたデータを1つずつ格納するリスト
     */
    private void parseLine(String readLineResult, StringBuffer stringBuffer, List<String> dataList) {

        for (int i = 0; i < readLineResult.length(); i++) {

            char currentChar = readLineResult.charAt(i);

            if (currentChar == '"') {
                doubleQuotationCount++;
            }

            if (doubleQuotationCount % 2 == 0 && currentChar == ',') {
                // 累計ダブルクォーテーションが偶数のときにカンマが来た場合
                // ダブルクォーテーションの外側に位置するカンマは,データの区切りである.
                // このカンマはappendせず,これまでバッファに入れていた文字をリストへ格納する

                // ※ダブルクォーテーションを含むデータの場合
                // エスケープ処理のダブルクォーテーションが末尾に1つだけ残るので,削除しておく
                if (stringBuffer.indexOf("\"") != -1) {
                    stringBuffer.deleteCharAt(stringBuffer.lastIndexOf("\""));
                }
                dataList.add(stringBuffer.toString());
                stringBuffer.setLength(0);

            } else if (doubleQuotationCount % 2 != 0 && currentChar == ',') {

                // 累計ダブルクォーテーションが奇数のときにカンマが来た場合
                // データそのものとしてのカンマである.まだ終わりではないのでカンマをappendする
                stringBuffer.append(currentChar);

            } else if (doubleQuotationCount % 2 == 0 && currentChar == '"') {

                // 累計ダブルクォーテーションが偶数になったとき
                // データそのものと見なし、このダブルクォーテーションをappendする
                stringBuffer.append(currentChar);

            } else if (doubleQuotationCount % 2 != 0 && currentChar == '"') {

                // 累計ダブルクォーテーションが奇数になったとき
                // こちらはエスケープ処理で増やした分のダブルクォートと見なし、appendしない

            } else {

                // ダブルクォート、カンマ以外の文字はデータそのものと見なし,appendする
                stringBuffer.append(currentChar);

            }

        }

        // 1行分すべてを読み終わったにもかかわらず、累計ダブルクォーテーションが奇数なとき
        // 途中に混ざった改行コードまでしか、BufferedReader.readLineメソッドで読み込みが出来ていない ⇒ レコードの終わりではない
        if (doubleQuotationCount % 2 != 0) {

            isRecordEnd = false;
            // データそのものとして入っていた改行コードはreadLineの結果には入っていないので補完する
            stringBuffer.append(LINE_SEPARATOR);

        } else {

            // 1行分すべてを読み終わり、累計ダブルクォーテーションが偶数のとき ⇒ レコードの最後に位置するデータの解析が完了

            // ※最後のデータがダブルクォーテーションを含んでいる場合
            // エスケープ処理のダブルクォートが末尾に1つだけ残るので,削除しておく
            if (stringBuffer.indexOf("\"") != -1) {
                stringBuffer.deleteCharAt(stringBuffer.lastIndexOf("\""));
            }
            dataList.add(stringBuffer.toString());

            stringBuffer.setLength(0);
            doubleQuotationCount = 0;
            isRecordEnd = true;

        }
    }
}

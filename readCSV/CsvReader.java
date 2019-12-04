package readCSV;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * [OJT-要素技術調査課題1] CSVファイル内のデータを読み込み、呼び出し元が利用できる形へ加工するクラス.
 *
 * @author W-nakayama
 */

public class CsvParser {
    /**
     * CSVファイルを読み込み、カンマで区切ったデータを二次元配列に格納して返却する.
     *
     * @param path パスをString型で引数に渡すことで,読み込むCSVファイルを指定する.
     * @return data2dArray カンマで区切ったCSVファイルのデータを二次元配列に格納して返却する.
     * @throws FileNotFoundException 呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IOException           ファイルの読み込みに失敗した場合
     */
    public String[][] convertTo2dArray(String path) throws FileNotFoundException, IOException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            /*
             * dataList splitedDataをコレクションするリスト splitedData
             * 読み込んだCSVファイルのデータを、カンマ毎に区切って格納した配列
             */
            List<String[]> dataList = new ArrayList<String[]>();
            String readLineResult;

            // 1行あたりの"カンマで区切られた要素の数"を保持する
            int numElements = 0;

            // ファイルの読み込み,dataListへの格納を終端まで1行ずつ実施する.
            while ((readLineResult = bufferedReader.readLine()) != null) {
                String[] splitedData = readLineResult.split(",");
                numElements = splitedData.length;
                dataList.add(splitedData);
            }

            // [dataListのサイズ]×[1行あたりの要素数(numElements)]で返却用の二次元配列を宣言
            String[][] data2dArray = new String[dataList.size()][numElements];
            for (int i = 0; i < dataList.size(); i++) {
                // dataArrayへの格納に用いる配列. 1行分のデータが入った配列をdataListから取得し,格納する
                String[] elements = dataList.get(i);
                for (int j = 0; j < elements.length; j++) {
                    data2dArray[i][j] = elements[j];
                }
            }
            return data2dArray;

        } catch (PatternSyntaxException e) {
            // splitメソッドにて、どの正規表現を使ってデータを区切るか指定する際に「+」「)」「}」を指定すると発生
            // 今回は「,」を指定している為この例外は発生しない.
            return new String[0][0];
        }

    }

}

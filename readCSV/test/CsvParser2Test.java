package readCSV;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class CsvParser10Test {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * カンマで区切られた2行×3列のデータを解析し,カンマを除いた部分のみ二次元配列に変換するテスト
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test
    public void productData() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        String[][] data2dArray = null;
        data2dArray = csvParser.convertTo2dArray("c:\\csvTest\\productData.csv");

        assertThat(data2dArray[0][0], is("productID"));
        assertThat(data2dArray[0][1], is("name"));
        assertThat(data2dArray[0][2], is("price"));

        assertThat(data2dArray[1][0], is("1"));
        assertThat(data2dArray[1][1], is("おいしい水"));
        assertThat(data2dArray[1][2], is("100"));
    }

    /**
     * データそのものにカンマが含まれているとき,そのカンマも含めて二次元配列に変換する.
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test
    public void dataWithComma() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        String[][] data2dArray = null;
        data2dArray = csvParser.convertTo2dArray("c:\\csvTest\\dataWithComma.csv");

        assertThat(data2dArray[0][0], is("東京, 名古屋, 大阪"));
        assertThat(data2dArray[0][1], is("横浜, 京都, 博多"));
        assertThat(data2dArray[0][2], is("神戸, 仙台, 札幌"));

        assertThat(data2dArray[1][0], is("03, 052, 06"));
        assertThat(data2dArray[1][1], is("045, 075, 0922"));
        assertThat(data2dArray[1][2], is("078, 022, 011"));
    }

    /**
     * データそのものに改行コードが含まれているとき,その改行コードも含めて二次元配列に変換する.
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test
    public void dataWithCRLF() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        String[][] data2dArray = null;
        data2dArray = csvParser.convertTo2dArray("c:\\csvTest\\dataWithCRLF.csv");

        assertThat(data2dArray[0][0], is("東京," + LINE_SEPARATOR + "名古屋," + LINE_SEPARATOR + "大阪"));
        assertThat(data2dArray[0][1], is("横浜, 京都, 博多"));
        assertThat(data2dArray[0][2], is("神戸, 仙台, 札幌"));

        assertThat(data2dArray[1][0], is("03, 052, 06"));
        assertThat(data2dArray[1][1], is("045," + LINE_SEPARATOR + "075," + LINE_SEPARATOR + "0922"));
        assertThat(data2dArray[1][2], is("078, 022, 011"));
    }

    /**
     * データそのものにダブルクォーテーションが含まれているとき,そのダブルクォーテーションも含めて二次元配列に変換する.
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test
    public void dataWithDoubleQuote() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        String[][] data2dArray = null;
        data2dArray = csvParser.convertTo2dArray("c:\\csvTest\\dataWithDoubleQuote.csv");

        assertThat(data2dArray[0][0], is("\"productID\""));
        assertThat(data2dArray[0][1], is("\"name\""));
        assertThat(data2dArray[0][2], is("\"price\""));

        assertThat(data2dArray[1][0], is("1"));
        assertThat(data2dArray[1][1], is("おいしい水"));
        assertThat(data2dArray[1][2], is("100"));
    }

    /**
     * 存在しないCSVファイルのパスを引数に渡した場合,IOExceptionをスローする.
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test(expected = IOException.class)
    public void wrongPath() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        csvParser.convertTo2dArray("c:\\csvTest\\hogehoge.csv");
    }

    /**
     * CSVファイル中で、1レコードあたりのデータの数が揃っていない場合,独自例外をスローする.
     *
     * @throws IOException               ファイルの読み込みに失敗した場合,
     *                                   呼び出し元から渡されたパス(引数)で指定されているファイルが見つからなかった場合
     * @throws IrregularColumnsException 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合
     */
    @Test(expected = IrregularColumnsException.class)
    public void irregularColsData() throws IOException, IrregularColumnsException {
        CsvParser2 csvParser = new CsvParser2();
        csvParser.convertTo2dArray("c:\\csvTest\\irregularColsData.csv");
    }

}

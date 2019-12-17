package readCSV;

public class IrregularColumnsException extends Exception {
    private static final long serialVersionUID = 1L;

    // 独自例外：解析したCSVファイルのデータ数が,レコード毎に異なっている場合にスローする.
    public IrregularColumnsException(String str) {
        super(str);
    }
}

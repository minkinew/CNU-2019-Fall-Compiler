package hw01;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eschough on 2019-09-02.
 */
public class NooToC {
    FileWriter fw;
    String nooPgm;
    StringBuilder sb = new StringBuilder(); // StringBuilder 객체 생성
    boolean flag = false; // flag 변수, 처음에 false

    // init
    public NooToC(FileWriter fw, String nooPgm) throws IOException { // 생성자
        this.fw = fw;
        this.nooPgm = nooPgm;
    }

    // translate cmd to C code for each case.
    public void translate() {
        String str = nooPgm; // str 변수에 nooPgm 파싱
        String words[] = str.split("\'"); // '을 기준으로 문자열을 구분하여 words배열에 저장

        List<String> listA = new ArrayList(); // String타입 리스트a 생성
        List<String> listB = new ArrayList(); // String타입 리스트B 생성
        int step = 1; // swtich문에 사용할 변수 step

        String firstString = "#include <stdio.h>\nint main() {\n\tint r, t1, t2, t3;\n"; // 처음으로 나타낼 문자열
        String lastString = "\treturn 1;\n}"; // 마지막에 나타낼 문자열
        listB.add(firstString); // listB에 firstString 추가

        for (int i = 0; i < words.length; i++) {
            if (words[i].length() == 5)  // '"""""일 때
                flag = true; // flag변수 true로 변경

            if (words[i].length() == 1)  // '" 일 때
                listA.add("\tprintf(\"%d\", r);\n"); // 해당 출력문 listA에 추가

            if (words[i].length() == 2)  // '"" 일 때
                listA.add("\tt1 = r;\n\tr =  t1 + 1;\n"); // 해당 출력문 listA에 추가

            if (words[i].length() == 3) { //'"" 일 때
                listA.add("\tr = 0;\n"); // 해당 출력문 listA에 추가
                for (int j = 0; j < listA.size(); j++) {
                    sb.append(listA.get(listA.size() - j - 1)); // listA에 거꾸로 붙임
                }

                if (flag == true && step < 4) { // '"""""일 때를 처리
                    switch (step) {
                        case 1: // step이 1일 때 t1=r과 if문 출력
                            sb.append("\tt1 = r;\n\tif (t1 != 0)\n\t{\n"); // append
                            break;
                        case 2: // step이 2일 때 if문 괄호를 닫고 else문 출력
                            sb.append("\t}\n\telse\n\t{\n"); // append
                            break;
                        case 3: // else문 괄호를 닫고 출력
                            sb.append("\t}\n");
                            break;
                    }
                    ++step; // step 변수 1 증가
                }

                listB.add(String.valueOf(sb)); // sb를 형변환하여 listB에 추가
                sb.setLength(0); // sb객체의 길이를 0으로 함
                listA.clear(); // listA를 clear
            }
        }

        listB.add(lastString); // listB에 lastString 추가
        for (int i = 0; i < listB.size(); i++) {
            try {
                fw.write(listB.get(i)); // 파일 쓰기
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

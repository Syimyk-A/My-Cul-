package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textViewEnter,textViewRes;

    Button buttonC,buttonSkob1,buttonSkob2,buttonDel,button7,button8,button9,
            buttonUmn,button6,button5,button4,buttonMin,button1,button2,button3,
            buttonPlus,button0,buttonPoint,buttonPros,buttonRavno,buttonProtsent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewEnter=findViewById(R.id.textViewEnter);
        textViewRes=findViewById(R.id.textViewRes);

        initButton(button0,R.id.button0);
        initButton(button1,R.id.button1);
        initButton(button2,R.id.button2);
        initButton(button3,R.id.button3);
        initButton(button4,R.id.button4);
        initButton(button5,R.id.button5);
        initButton(button6,R.id.button6);
        initButton(button7,R.id.button7);
        initButton(button8,R.id.button8);
        initButton(button9,R.id.button9);
        initButton(buttonC,R.id.buttonC);
        initButton(buttonSkob1,R.id.buttonSkob1);
        initButton(buttonSkob2,R.id.buttonSkob2);
        initButton(buttonDel,R.id.buttonDel);
        initButton(buttonMin,R.id.buttonMin);
        initButton(buttonUmn,R.id.buttonUmn);
        initButton(buttonPlus,R.id.buttonPlus);
        initButton(buttonPoint,R.id.buttonPoint);
        initButton(buttonPros,R.id.buttonPros);
        initButton(buttonRavno,R.id.buttonRavno);
        initButton(buttonProtsent,R.id.buttonProtsent);
    }

    void initButton(Button button,int id){
        button=findViewById(id);
        button.setOnClickListener(this::onClick);
    }
    @Override
    public void onClick(View v) {
        Button button =(Button) v;
        String btnText=button.getText().toString();
        String data = textViewEnter.getText().toString();



        if(btnText.equals("AC")){
            textViewEnter.setText("");
            textViewRes.setText("");
            return;
        }

        if(btnText.equals("C")) {
            if(data.length() != 0 && !data.equals("0"))
                data = data.substring(0, data.length() - 1);
            else
                data = "0";

            textViewEnter.setText(data); // Обновляем строку, а не добавляем символы
            return;
        }


        // Проверка на дублирование операторов
        if ("+-*/".contains(btnText)) {
            if (data.isEmpty() || "*/+".contains(Character.toString(data.charAt(data.length() - 1)))) {
                // Если последний символ — это оператор, заменим его на новый
                data = data.substring(0, data.length() - 1) + btnText;
            } else {
                data += btnText;
            }
            textViewEnter.setText(data);
            return;
        }


        if (btnText.equals("%")) {
            if (!data.isEmpty()) {
                try {
                    double value = Double.parseDouble(data);
                    value = value / 100;  // Преобразование числа в процент
                    textViewEnter.setText(String.valueOf(value));
                } catch (NumberFormatException e) {
                    textViewRes.setText("Error");
                }
            }
            return; // Прерываем дальнейшую обработку для процента
        }


        // Проверка парности скобок
        if (btnText.equals("(")) {
            // Разрешаем открывать скобку в любом месте
            data += btnText;
            textViewEnter.setText(data);
            return;
        }

        if (btnText.equals(")")) {
            int openBrackets = countOccurrences(data, '(');
            int closeBrackets = countOccurrences(data, ')');
            if (openBrackets > closeBrackets && !data.endsWith("(") && !"*/+-".contains(Character.toString(data.charAt(data.length() - 1)))) {
                // Закрываем скобку только если открытых скобок больше и предыдущий символ не оператор
                data += btnText;
            }
            textViewEnter.setText(data);
            return;
        }


        if(btnText.equals("=")){
            if (btnText.equals("=")) {
                String finalResult = evaluateExpression(data);
                if (!finalResult.equals("Error")) {
                    textViewRes.setText(finalResult);
                } else {
                    textViewRes.setText("Error");
                }
                return; // Прерываем дальнейшую обработку, т.к. равно уже нажато
            }

            return;
        }
        if (data.equals("0")) {
            data = "";
        }

        data+=btnText;
        textViewEnter.setText(data);



       // Log.i("result", finalresult);
    }

    // Метод для подсчета количества символов в строке
    private int countOccurrences(String data, char symbol) {
        int count = 0;
        for (char c : data.toCharArray()) {
            if (c == symbol) count++;
        }
        return count;
    }


    private String evaluateExpression(String expression) {
        // Создаем контекст Rhino
        Context rhino = Context.enter();
        // Устанавливаем версию JavaScript, которую будем использовать (по умолчанию актуальная)
        rhino.setOptimizationLevel(-1); // Без оптимизации для мобильных устройств
        try {
            // Создаем скриптовый объект Rhino
            Scriptable scope = rhino.initStandardObjects();
            // Выполняем выражение JavaScript
            String result = rhino.evaluateString(scope, expression, "JavaScript", 1, null).toString();
            // Приводим результат к числу и возвращаем его

            DecimalFormat decimalFormat= new DecimalFormat("#.###");
            return decimalFormat.format(Double.parseDouble(result));
        }
        catch(Exception e){
            return "Error";
        }
        finally {
            // Выход из контекста Rhino, освобождаем ресурсы
            Context.exit();
        }
    }

}
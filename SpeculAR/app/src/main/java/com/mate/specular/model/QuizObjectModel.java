package com.mate.specular.model;

public class QuizObjectModel implements ObjectModel {
    private String content;
    private String ans1;
    private String ans2;
    private String ans3;
    private String ans4;
    private String correct;
    private double x_coord;
    private double y_coord;

    public QuizObjectModel(String content, String ans1, String ans2, String ans3, String ans4, String correct) {
        this.content = content;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAns1() {
        return ans1;
    }

    public void setAns1(String ans1) {
        this.ans1 = ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public void setAns2(String ans2) {
        this.ans2 = ans2;
    }

    public String getAns3() {
        return ans3;
    }

    public void setAns3(String ans3) {
        this.ans3 = ans3;
    }

    public String getAns4() {
        return ans4;
    }

    public void setAns4(String ans4) {
        this.ans4 = ans4;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    @Override
    public double getX_coord() {
        return x_coord;
    }

    @Override
    public void setX_coord(double x_coord) {
        this.x_coord = x_coord;
    }

    @Override
    public double getY_coord() {
        return y_coord;
    }

    @Override
    public void setY_coord(double y_coord) {
        this.y_coord = y_coord;
    }

}

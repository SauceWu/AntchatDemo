package me.example.sauce.anychattest;

/**
 * Version ${versionName}
 * Created by sauce on 16/10/19.
 */

public class BankCardBean {

    /**
     * card_number : 436745009302
     * validate : 1976 年 11 月 19 日
     * “holder_name” : ”SHEN DONGHUI”
     */

    private String card_number;
    private String validate;
    private String holder_name;

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public String getHolder_name() {
        return holder_name;
    }

    public void setHolder_name(String holder_name) {
        this.holder_name = holder_name;
    }

    @Override
    public String toString() {
        return "BankCardBean{" +
                "card_number='" + card_number + '\'' +
                ", validate='" + validate + '\'' +
                ", holder_name='" + holder_name + '\'' +
                '}';
    }
}

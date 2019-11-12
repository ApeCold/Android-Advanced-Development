package com.netease.common.order;

/**
 * {"resultcode":"200","reason":"查询成功",
 * "result":{"Country":"美国","Province":"加利福尼亚","City":"","Isp":""},"error_code":0}
 */
public class OrderBean {

    private String resultcode;
    private String reason;
    private Result result;
    private int error_code;

    public static class Result {

        private String Country;
        private String Province;
        private String City;
        private String Isp;

        public String getCountry() {
            return Country;
        }

        public void setCountry(String country) {
            Country = country;
        }

        public String getProvince() {
            return Province;
        }

        public void setProvince(String province) {
            Province = province;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String city) {
            City = city;
        }

        public String getIsp() {
            return Isp;
        }

        public void setIsp(String isp) {
            Isp = isp;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "Country='" + Country + '\'' +
                    ", Province='" + Province + '\'' +
                    ", City='" + City + '\'' +
                    ", Isp='" + Isp + '\'' +
                    '}';
        }
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "resultcode='" + resultcode + '\'' +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                ", error_code=" + error_code +
                '}';
    }
}

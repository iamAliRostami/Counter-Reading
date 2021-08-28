package com.leon.counter_reading.utils;


import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.enums.CompanyNames;

public class DifferentCompanyManager {

    public static CompanyNames getActiveCompanyName() {
//        return CompanyNames.ZONE4;
        return BuildConfig.COMPANY_NAME;
    }

    public static String getBaseUrl(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "https://37.191.92.157/";
            case ZONE1:
                return "http://217.146.220.33:50011/";
            case ZONE2:
                return "http://212.16.75.194:8080/";
            case ZONE3:
                return "http://212.16.69.36:90/";
            case ZONE4:
                return "http://81.12.106.167:8081/";
            case ZONE5:
                return "http://80.69.252.151/";
            case ZONE6:
                return "http://85.133.190.220:4121/";
            case TSW:
                return "http://81.90.148.25/";
            case TE:
                return "http://185.120.137.254";
            case TSE:
                return "http://5.160.85.228:9098/";
            case TOWNS_WEST:
                return "http://217.66.195.75/";
            case DEBUG:
                return "http://192.168.43.185:45458/";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getLocalBaseUrl(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "http://172.18.12.14:100";
            case ESF_MAP:
                return "http://172.18.12.242/osm_tiles/";
            case ZONE1:
                return "http://172.21.0.16/";
            case ZONE2:
                return "http://172.22.4.71/";
            case ZONE3:
                return "http://172.23.0.113/";
            case ZONE4:
                return "http://172.24.13.23/";
            case ZONE5:
                return "http://172.25.0.72/";
            case ZONE6:
                return "http://172.26.0.32/";
            case TSW:
                return "http://172.30.1.22/";
            case TE:
                return "http://172.31.0.25/";
            case TSE:
                return "http://172.28.5.40/";
            case TOWNS_WEST:
                return "http://172.28.5.41/";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getSiteAddress(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "abfaEsfahan.ir";
            case ZONE4:
                return "t4ww.tpww.ir";
            case TSE:
                return "swest.tpww.ir";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد";
            case ZONE4:
            case TSE:
                return "آحاد";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad1(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد مسکونی";
            case ZONE4:
            case TSE:
                return "آحاد اصلی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhad2(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد تجاری";
            case ZONE4:
            case TSE:
                return "آحاد فرعی";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getAhadTotal(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "واحد کل";
            case ZONE4:
            case TSE:
                return "آحاد مصرف";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static int getImageNumber(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return 4;
            case ZONE4:
            case TSE:
                return 4;
            default:
                return 4;
        }
    }

    public static int getShowError(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return 3;
            case ZONE4:
            case TSE:
                return 3;
            default:
                return 3;
        }
    }

    public static int getLockNumber(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return 6;
            case ZONE4:
            case TSE:
                return 6;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static int getEshterakMinLength(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return 5;
            case ZONE4:
            case TSE:
                return 5;
            default:
                return 10;
        }
    }

    public static int getEshterakMaxLength(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return 15;
            case ZONE4:
            case TSE:
                return 15;
            default:
                return 10;
        }
    }

    public static String getSecondSearchItem(CompanyNames companyNames) {
        switch (companyNames) {
            case ESF:
                return "ردیف";
            case ZONE4:
            case TSE:
                return "شماره پرونده";
            default:
                return "دیگر";
        }
    }

    public static CompanyNames getCompanyNameEnum(int companyCode) {
        switch (companyCode) {
            case 1:
                return CompanyNames.ZONE1;
            case 2:
                return CompanyNames.ZONE2;
            case 3:
                return CompanyNames.ZONE3;
            case 4:
                return CompanyNames.ZONE4;
            case 5:
                return CompanyNames.ZONE5;
            case 6:
                return CompanyNames.ZONE6;
            case 7:
                return CompanyNames.TSW;
            case 8:
                return CompanyNames.TE;
            case 9:
                return CompanyNames.TSE;
            case 10:
                return CompanyNames.TOWNS_WEST;
            case 11:
                return CompanyNames.ESF;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getCompanyName(CompanyNames companyName) {
        switch (companyName) {
            case ZONE1:
                return "آبقا منطقه یک";
            case ZONE2:
                return "آبفا منطقه2";
            case ZONE3:
                return "آبفا منطقه سه";
            case ZONE4:
                return "آبفا منطقه چهار";
            case ZONE5:
                return "آبفامنطقه پنج";
            case ZONE6:
                return "آبقا منطقه شش";
            case TE:
                return "آبفا شرق";
            case TSW:
                return "آبفا جنوب غربی";
            case TSE:
                return "آبفا جنوب شرقی";
            case TOWNS_WEST:
                return "آبفا شهرک های غرب";
            case ESF:
                return "آبفا استان اصفهان";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getMapUrl(CompanyNames companyNames) {
        switch (companyNames) {
            case ZONE1:
                return "http://217.146.220.33:50011/";
            case ZONE2:
                return "http://212.16.75.194:8080/";
            case ZONE3:
                return "http://212.16.69.36:90/";
            case ZONE4:
                return "http://91.98.248.36:8081/";
            case ZONE5:
                return "http://80.69.252.151/";
            case ZONE6:
                return "http://85.133.190.220:4121/";
            case TSW:
                return "http://81.90.148.25/";
            case TE:
                return "http://185.120.137.254";
            case TSE:
                return "http://5.160.85.228:9098/";
            case TOWNS_WEST:
                return "http://217.66.195.75/";
            case ESF:
                return "http://37.191.92.130/";
            case DEBUG:
                return "http://192.168.43.185:45458/";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getEmailTail(CompanyNames companyNames) {
        switch (companyNames) {
            case ZONE1:
                return "@zone1.ir";
            case ZONE2:
                return "@zone2.ir";
            case ZONE3:
                return "@zone3.ir";
            case ZONE4:
                return "@zone4.ir";
            case ZONE5:
                return "@zone5.ir";
            case ZONE6:
                return "@zone6.ir";
            case TSW:
                return "@swt.ir";
            case TE:
                return "@te.ir";
            case TSE:
                return "@tse.ir";
            case TOWNS_WEST:
                return "@ttw.ir";
            case ESF:
                return "@esf.ir";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getCameraUploadUrl(CompanyNames companyNames) {
        switch (companyNames) {
            case ZONE1:
                return "http://217.146.220.33:50011/Api/api/CRMobileOffLoad/Upload";
            case ZONE2:
                return "http://212.16.75.194:8080/Api/api/CRMobileOffLoad/Upload";
            case ZONE3:
                return "http://212.16.69.36:90/Api/api/CRMobileOffLoad/Upload";
            case ZONE4:
                return "http://91.98.248.36:8081/Api/api/CRMobileOffLoad/Upload";
            case ZONE5:
                return "http://80.69.252.151/Api/api/CRMobileOffLoad/Upload";
            case ZONE6:
                return "http://85.133.190.220:4121/Api/api/CRMobileOffLoad/Upload";
            case TSW:
                return "http://81.90.148.25/Api/api/CRMobileOffLoad/Upload";
            case TE:
                return "http://185.12.60.135/Api/api/CRMobileOffLoad/Upload";
            case TSE:
                return "http://5.160.85.228:9098/Api/api/CRMobileOffLoad/Upload";
            case TOWNS_WEST:
                return "http://217.66.195.75/Api/api/CRMobileOffLoad/Upload";
            case ESF:
                return "http://172.18.12.122/Api/api/CRMobileOffLoad/Upload";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getPrefixName(CompanyNames companyNames) {
        switch (companyNames) {
            case ZONE1:
                return "@zone1.ir";
            case ZONE2:
                return "@zone2.ir";
            case ZONE3:
                return "@zone3.ir";
            case ZONE4:
                return "@zone4.ir";
            case ZONE5:
                return "@zone5.ir";
            case ZONE6:
                return "@zone6.ir";
            case TSW:
                return "tsw_";
            case TE:
                return "@te.ir";
            case TSE:
                return "@tse.ir";
            case TOWNS_WEST:
                return "@ttw.ir";
            case ESF:
                return "@esf.ir";
            default:
                throw new UnsupportedOperationException();
        }
    }
}

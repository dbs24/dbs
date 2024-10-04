package org.dbs.ref.serv.enums

import org.dbs.ref.serv.enums.CountryEnum.BY
import org.dbs.ref.serv.enums.CountryEnum.CA
import org.dbs.ref.serv.enums.CountryEnum.CN
import org.dbs.ref.serv.enums.CountryEnum.MX
import org.dbs.ref.serv.enums.CountryEnum.PL
import org.dbs.ref.serv.enums.CountryEnum.US

enum class RegionEnum(
    private val regionId: Int,
    private val regionName: String,
    private val domesticRegionCode: String,
    private val country: CountryEnum
) {

    AB(1240001, "Alberta", "AB", CA),
    BC(1240002, "British Columbia", "BC", CA),
    MB(1240003, "Manitoba", "MB", CA),
    NB(1240004, "New Brunswick", "NB", CA),
    NL(1240005, "Newfoundland and Labrador", "NL", CA),
    NS(1240006, "Nova Scotia", "NS", CA),
    ON(1240007, "Ontario", "ON", CA),
    PE(1240008, "Prince Edward Island", "PE", CA),
    QC(1240009, "Quebec", "QC", CA),
    SK(1240010, "Saskatchewans", "SK", CA),
    NT(1240011, "Northwest Territories", "NT", CA),
    NU(1240012, "Nunavut", "N.U.", CA),
    YT(1240013, "Yukon", "YT", CA),

    ID(8400001, "Idaho", "ID", US),
    IA(8400002, "Iowa", "IA", US),
    AL(8400003, "Alabama", "AL", US),
    AK(8400004, "Alaska", "AK", US),
    AZ(8400005, "Arizona", "AZ", US),
    AR(8400006, "Arkansas", "AR", US),
    WY(8400007, "Wyoming", "WY", US),
    WA(8400008, "Washington", "WA", US),
    VT(8400009, "Vermont", "VT", US),
    VA(8400010, "Virginia", "VA", US),
    WI(8400011, "Wisconsin", "WI", US),
    HI(8400012, "Hawaii", "HI", US),
    DE(8400013, "Delaware", "DE", US),
    GA(8400014, "Georgia", "GA", US),
    WV(8400015, "West Virginia", "WV", US),
    IL(8400016, "Illinois", "IL", US),
    IN(8400017, "Indiana", "IN", US),
    CA_US(8400018, "California", "CA", US),
    KS(8400019, "Kansas", "KS", US),
    KY(8400020, "Kentucky", "KY", US),
    CO(8400021, "Colorado", "CO", US),
    CT(8400022, "Connecticut", "CT", US),
    LA(8400023, "Louisiana", "LA", US),
    MA(8400024, "Massachusetts", "MA", US),
    MN(8400025, "Minnesota", "MN", US),
    MS(8400026, "Mississippi", "MS", US),
    MO(8400027, "Missouri", "MO", US),
    MI(8400028, "Michigan", "MI", US),
    MT(8400029, "Montana", "MT", US),
    ME(8400030, "Maine", "ME", US),
    MD(8400031, "Maryland", "MD", US),
    NE(8400032, "Nebraska", "NE", US),
    NV(8400033, "Nevada", "NV", US),
    NH(8400034, "New Hampshire", "NH", US),
    NJ(8400035, "New Jersey", "NJ", US),
    NY(8400036, "New York", "NY", US),
    NM(8400037, "New Mexico", "NM", US),
    OH(8400038, "Ohio", "OH", US),
    OK(8400039, "Oklahoma", "OK", US),
    OR(8400040, "Oregon", "OR", US),
    PA(8400041, "Pennsylvania", "PA", US),
    FL(8400042, "Florida Peninsula", "FL", US),
    RI(8400043, "Rhode Island", "RI", US),
    ND(8400044, "North Dakota", "ND", US),
    NC(8400045, "North Carolina", "NC", US),
    TN(8400046, "Tennessee", "TN", US),
    TX(8400047, "Texas", "TX", US),
    SD(8400048, "South Dakota", "SD", US),
    SC(8400049, "South Carolina", "SC", US),
    UT(8400050, "Utah", "UT", US),

    AGU(4840001, "Aguascalientes", "AGU", MX),
    VER(4840002, "Veracruz", "VER", MX),
    GRO(4840003, "Guerrero", "GRO", MX),
    GUA(4840004, "Guanajuato", "GUA", MX),
    DUR(4840005, "Durango", "DUR", MX),
    HID(4840006, "Hidalgo", "HID", MX),
    CAM(4840007, "Campeche", "CAM", MX),
    QUE(4840008, "Queretaro", "QUE", MX),
    ROO(4840009, "Quintana Roo", "ROO", MX),
    COA(4840010, "Coahuila", "COA", MX),
    COL(4840011, "Colima", "COL", MX),
    MEX(4840012, "Mexico", "MEX", MX),
    MIC(4840013, "Michoacán", "MIC", MX),
    MOR(4840014, "Morelos", "MOR", MX),
    NAY(4840015, "Nayarit", "NAY", MX),
    BCN(4840016, "Baja California", "BCN", MX),
    NLE(4840017, "Nuevo Leon", "NLE", MX),
    OAX(4840018, "Oaxaca", "OAX", MX),
    PUE(4840019, "Puebla", "PUE", MX),
    ZAC(4840020, "Zacatecas", "ZAC", MX),
    SLP(4840021, "San Luis Potosi", "SLP", MX),
    SIN(4840022, "Sinaloa", "SIN", MX),
    SON(4840023, "Sonora", "SON", MX),
    TAB(4840024, "Tabasco", "TAB", MX),
    TAM(4840025, "Tamaulipas", "TAM", MX),
    TLA(4840026, "Tlaxcala", "TLA", MX),
    SMX(4840027, "Federal District", "SMX", MX),
    JAL(4840028, "Jalisco", "JAL", MX),
    CNN(4840029, "Chihuahua", "CNN", MX),
    CHP(4840030, "Chiapas", "CHP", MX),
    BCS(4840031, "Baja California Sur", "BCS", MX),
    YUC(4840032, "Yucatan", "YUC", MX),

    BR(1120001, "Brest region", "BR", BY),
    HO(1120002, "Gomel region", "HO", BY),
    HR(1120003, "Grodno region", "HR", BY),
    MI_BY(1120004, "Minsk Region", "MI", BY),
    MA_BY(1120005, "Mogilev region", "MA", BY),
    VI(1120006, "Vitebsk region", "VI", BY),

    PL_30(6160001, "Wielkopolskie", "PL-30", PL),
    PL_04(6160002, "Kujawsko-pomorskie", "PL-04", PL),
    PL_12(6160003, "Malopolskie", "PL-12", PL),
    PL_10(6160004, "Lodzkie", "PL-10", PL),
    PL_02(6160005, "Dolnoslaskie", "PL-02", PL),
    PL_06(6160006, "Lubelskie", "PL-06", PL),
    PL_08(6160007, "Lubuskie", "PL-08", PL),
    PL_14(6160008, "Mazowieckie", "PL-14", PL),
    PL_16(6160009, "Opolskie", "PL-16", PL),
    PL_20(6160010, "Podlaskie", "PL-20", PL),
    PL_22(6160011, "Pomorskie", "PL-22", PL),
    PL_24(6160012, "Śląskie", "PL-24", PL),
    PL_18(6160013, "Podkarpackie", "PL-18", PL),
    PL_26(6160014, "Swietokrzyskie", "PL-26", PL),
    PL_28(6160015, "Warminsko-mazurskie", "PL-28", PL),
    PL_32(6160016, "Zachodniopomorskie", "PL-32", PL),

    HE(1560001, "Hebei", "HE", CN),
    SN(1560002, "Shanxi", "SN", CN),
    LN(1560003, "Liaoning", "LN", CN),
    JL(1560004, "Jilin", "JL", CN),
    HL(1560005, "Heilongjiang", "HL", CN),
    GS(1560006, "Jiangsu", "GS", CN),
    ZJ(1560007, "Zhejiang", "ZJ", CN),
    AH(1560008, "Anhui", "AH", CN),
    FJ(1560009, "Fujian", "FJ", CN),
    JX(1560010, "Jiangxi", "JX", CN),
    HK(1560011, "Shandong", "HK", CN),
    HN(1560012, "Henan", "HN", CN),
    HB(1560013, "Hubei", "HB", CN),
    HI_CN(1560014, "Hunan", "HI", CN),
    GD(1560015, "Guangdong", "GD", CN),
    HA(1560016, "Hainan", "HA", CN),
    SC_CN(1560017, "Sichuan", "SC", CN),
    GZ(1560018, "Guizhou", "GZ", CN),
    YN(1560019, "Yunnan", "YN", CN),
    SN_CN(1560020, "Shaanxi", "SN", CN),
    GX(1560021, "Gansu", "GX", CN),
    QH(1560022, "Qinghai", "QH", CN),
    TW(1560023, "Taiwan", "TW", CN),
    NM_CN(1560024, "Inner Mongolia", "NM", CN),
    GX_CN(1560025, "Guangxi", "GX", CN),
    TI(1560026, "Tibet", "TI", CN),
    NX(1560027, "Ningxia", "NX", CN),
    XJ(1560028, "Xinjiang", "XJ", CN),
    BJ(1560029, "Beijing", "BJ", CN),
    SH(1560030, "Shanghai", "SH", CN),
    TJ(1560031, "Tianjin", "TJ", CN),
    CQ(1560032, "Chongqing", "CQ", CN);

    companion object {
        //==================================================================================================================
        //@get:Synchronized

        fun getEnum(regionId: Int): RegionEnum =
            entries.stream()
                .filter { it.regionId == regionId }
                .findFirst()
                .orElseThrow()

        fun getEnum(regionDomesticCode: String): RegionEnum = entries.stream()
            .filter { it.domesticRegionCode == regionDomesticCode }
            .findFirst()
            .orElseThrow()

        fun getEnum(regionDomesticCode: String, country: CountryEnum): RegionEnum =
            RegionEnum.entries.stream()
                .filter { (it.domesticRegionCode == regionDomesticCode && it.getCountry() == country) }
                .findFirst()
                .orElseThrow()

        fun getByCountryIso(countryIso: String): Set<RegionEnum> =
            RegionEnum.entries.filter { it.country.getCountryIso() == countryIso }.toSet()

        fun isExistRegion(regionDomesticCode: String) = entries.stream()
            .filter { it.getRegionDomesticCode() == regionDomesticCode }
            .findFirst()
            .isPresent

        fun isExistEnum(id: Int) = entries.any { it.getRegionId() == id }

        val regionDomesticCodes: Collection<String> by lazy {
            entries
                .map(RegionEnum::domesticRegionCode)
        }

        val regionIds: Collection<Int>
            get() = RegionEnum.entries
                .map(RegionEnum::regionId)


    }

    fun getRegionName() = this.regionName

    fun getRegionDomesticCode() = this.domesticRegionCode

    fun getRegionId() = this.regionId

    fun getCountry(): CountryEnum = this.country
}

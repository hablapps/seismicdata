package dev.habla.seismicdata

package object protocol{

  val stationNames = List(
	("ES", "AFON"),
	("ES", "ATANC"),
	("ES", "CADE"),
	("ES", "CBLA"),
	("ES", "CBOL"),
	("ES", "CBRE"),
	("ES", "CCAL"),
	("ES", "CCAN"),
	("ES", "CCHO"),
	("ES", "CCUM"),
	("ES", "CDIE"),
	("ES", "CDLV"),
	("ES", "CDOS"),
	("ES", "CENR"),
	("ES", "CFOR"),
	("ES", "CFTV"),
	("ES", "CFUE"),
	("ES", "CGIN"),
	("ES", "CGOR"),
	("ES", "CGRA"),
	("ES", "CGUI"),
	("ES", "CIBOR"),
	("ES", "CJED"),
	("ES", "CJUL"),
	("ES", "CLAJ"),
	("ES", "CLUM"),
	("ES", "CMCL"),
	("ES", "CMIR"),
	("ES", "CNAO"),
	("ES", "CNOR"),
	("ES", "CPUN"),
	("ES", "CPVI"),
	("ES", "CRAJ"),
	("ES", "CREA"),
	("ES", "CROM"),
	("ES", "CTAB"),
	("ES", "CTAC"),
	("ES", "CTAN"),
	("ES", "CTEN"),
	("ES", "CTFS"),
	("ES", "CTIG"),
	("ES", "CTIM"),
	("ES", "CVIE"),
	("ES", "CVIL"),
	("ES", "E0803"),
	("ES", "E0901"),
	("ES", "E1201"),
	("ES", "E1202"),
	("ES", "E1302"),
	("ES", "EADA"),
	("ES", "EAGO"),
	("ES", "EALB"),
	("ES", "EALK"),
	("ES", "EARA"),
	("ES", "EARI"),
	("ES", "EBAD"),
	("ES", "EBAJ"),
	("ES", "EBEN2"),
	("ES", "EBER"),
	("ES", "ECAB"),
	("ES", "ECAL"),
	("ES", "ECEU"),
	("ES", "ECHE"),
	("ES", "ECHI"),
	("ES", "ECOL"),
	("ES", "EFAM"),
	("ES", "EGOM"),
	("ES", "EGOR"),
	("ES", "EGRO"),
	("ES", "EHIG"),
	("ES", "EIBI"),
	("ES", "EJIF"),
	("ES", "EJON"),
	("ES", "EJUZ"),
	("ES", "ELAN"),
	("ES", "ELGU"),
	("ES", "ELOB"),
	("ES", "ELOR"),
	("ES", "EMAZ"),
	("ES", "EMIJ"),
	("ES", "EMIN"),
	("ES", "EMIR"),
	("ES", "EMLI"),
	("ES", "EMOS"),
	("ES", "EMUR"),
	("ES", "ENIJ"),
	("ES", "EORO"),
	("ES", "EOSO"),
	("ES", "EPLA"),
	("ES", "EPOB"),
	("ES", "EPON"),
	("ES", "EQES"),
	("ES", "EQTA"),
	("ES", "ERTA"),
	("ES", "ES01"),
	("ES", "ES02"),
	("ES", "ES03"),
	("ES", "ES04"),
	("ES", "ES05"),
	("ES", "ES06"),
	("ES", "ES07"),
	("ES", "ES08"),
	("ES", "ES09"),
	("ES", "ES10"),
	("ES", "ES11"),
	("ES", "ES12"),
	("ES", "ES13"),
	("ES", "ES14"),
	("ES", "ES15"),
	("ES", "ES16"),
	("ES", "ES17"),
	("ES", "ES18"),
	("ES", "ES19"),
	("ES", "ESAC"),
	("ES", "ESBB"),
	("ES", "ESBB1"),
	("ES", "ESPR"),
	("ES", "ETOB"),
	("ES", "ETOS"),
	("ES", "ETRV"),
	("ES", "EVIV"),
	("ES", "EZAM"),
	("ES", "EZAR"),
	("ES", "GGC"),
	("ES", "GUD"),
	("ES", "GUIA"),
	("ES", "PSIM"),
	("ES", "RETOR"),
	("ES", "STS"),
	("ES", "TBT"),
	("ES", "TLOR"),
	("ES", "UMV12"),
	("ES", "VPORT"),
	("ES", "YEBES"))


   val remoteStationNames = List(
("ES","CBOL"),
("ES","EXSEU"),
("ES","ECOL"),
("ES","AFON"),
("ES","EHIG"),
("ES","EAGO"),
("ES","ES19"),
("ES","ES18"),
("ES","ES13"),
("ES","ES12"),
("ES","ES11"),
("ES","ES10"),
("ES","ES17"),
("ES","ES16"),
("ES","ES15"),
("ES","ES14"),
("ES","SX521"),
("ES","SX520"),
("ES","CENR"),
("ES","EMUR"),
("ES","EALB"),
("ES","EPLA"),
("ES","CNAO"),
("ES","EXQUE"),
("ES","EXPDH"),
("ES","EALK"),
("ES","E0901"),
("ES","EXMAD"),
("ES","CBLA"),
("ES","EXLO2"),
("ES","EXCA"),
("ES","EXEL"),
("ES","EGOM"),
("ES","EJUZ"),
("ES","EMLI"),
("ES","EVIV"),
("ES","CCAL"),
("ES","EXHUE"),
("ES","CCAN"),
("ES","CNOR"),
("ES","CGIN"),
("ES","SX530"),
("ES","SX531"),
("ES","CINF"),
("ES","EXILP"),
("ES","CDOS"),
("ES","EBER"),
("ES","RETOR"),
("ES","CFTV"),
("ES","EXCAB"),
("ES","ECIB"),
("ES","CTAB"),
("ES","CTAC"),
("ES","SX909"),
("ES","GUD"),
("ES","CTAN"),
("ES","SX906"),
("ES","EQES"),
("ES","ETOS"),
("ES","SX901"),
("ES","EJIF"),
("ES","EXMOR"),
("ES","CTIM"),
("ES","CTIG"),
("ES","ETOB"),
("ES","ESPR"),
("X3","ALT04"),
("ES","CFOR"),
("ES","SX018"),
("X3","ALT02"),
("X3","ALT03"),
("ES","CCUM"),
("ES","SX014"),
("ES","SX017"),
("ES","SX010"),
("ES","SX013"),
("ES","SX012"),
("ES","EOSO"),
("ES","EXBUL"),
("XZ","TEST1"),
("X2","YMUS"),
("XZ","TEST2"),
("ES","ECHE"),
("ES","CLUM"),
("ES","ECHI"),
("X2","YUND"),
("ES","EADA"),
("ES","SX038"),
("ES","EGRO"),
("ES","CJED"),
("ES","EXAGE"),
("ES","SX008"),
("ES","ECAL"),
("ES","SX006"),
("ES","ECAB"),
("ES","SX004"),
("ES","EXCBR"),
("ES","SX002"),
("ES","SX003"),
("ES","SX001"),
("ES","EPOB"),
("ES","EXAGR"),
("ES","ESAC"),
("ES","CREA"),
("X3","ALT01"),
("ES","EPON"),
("ES","EIBI"),
("X2","YSOS"),
("ES","ELOR"),
("ES","SX037"),
("ES","EJON"),
("ES","EMAZ"),
("ES","ERTA"),
("ES","EBEN2"),
("ES","EXEJI"),
("ES","CROM"),
("ES","ELOB"),
("ES","EXAHM"),
("ES","EQTA"),
("ES","ES02"),
("ES","SX039"),
("ES","EMIJ"),
("ES","CTFS"),
("ES","CMCL"),
("ES","EMIN"),
("ES","ES04"),
("ES","SX908"),
("ES","EZAR"),
("ES","SX036"),
("ES","SX035"),
("ES","CGUI"),
("ES","EZAM"),
("IU","MACI"),
("ES","GGC"),
("ES","CLGU"),
("ES","EXGRA"),
("ES","EXMAZ"),
("ES","EMIR"),
("ES","SX905"),
("IU","PAB"),
("ES","TLOR"),
("ES","ESBB"),
("ES","EFAM"),
("ES","SX902"),
("ES","SX024"),
("ES","SX025"),
("ES","SX026"),
("ES","CARI"),
("ES","SX027"),
("ES","SX022"),
("ES","SX023"),
("ES","EXSFS"),
("ES","ENIJ"),
("ES","ELGU"),
("ES","EARI"),
("ES","SX101"),
("ES","EARA"),
("ES","EXTPC"),
("ES","EBAD"),
("ES","PSIM"),
("ES","EXAYA"),
("ES","EBAJ"),
("ES","EXGUA"),
("X2","YSIG"),
("ES","SX005"),
("ES","STS"),
("ES","CCHO"),
("ES","CDIE"),
("ES","EMOS"),
("ES","TBT"),
("ES","CPUN"),
("X2","YSAN"),
("ES","ELAN"),
("ES","CADE"),
("ES","CMIR"),
("ES","ETRV"),
("ES","GUIA"),
("ES","SX509"),
("ES","CFUE"),
("ES","EXJIF"),
("ES","EXRIP"),
("ES","E1302"),
("ES","ESBB1"),
("ES","VPORT"),
("ES","CLAJ"),
("ES","E0802"),
("ES","E1201"),
("ES","CDLV"),
("ES","SX911"),
("ES","ES08"),
("ES","ES09"),
("ES","SX040"),
("ES","SX041"),
("ES","CGOR"),
("ES","CBRE"),
("ES","ES01"),
("ES","CPVI"),
("ES","ES03"),
("ES","CJUL"),
("ES","ES05"),
("ES","ES06"),
("ES","ES07"),
("ES","YEBES"),
("ES","EXBAR"),
("ES","CGRA"),
("ES","EORO"),
("ES","SX015"),
("ES","ECEU"),
("ES","EGOR"),
("ES","CVIL"),
("ES","CTEN"),
("ES","CVIE"),
("ES","CRAJ"))

}
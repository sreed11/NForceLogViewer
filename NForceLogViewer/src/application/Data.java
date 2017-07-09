package application;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Data {
	//Date and time Simple Properties and regular variables
	private int yearI,monthI,dayI,hourI,minuteI;
	private float secondI;
	private SimpleStringProperty date, time;
	private SimpleIntegerProperty year,month,day,hour,minute;
	private SimpleFloatProperty second;
	//Compressor Simple Properties and regular variables
	private SimpleStringProperty mvcc, crl, pcr, compSyn, bc, 
		cta, mr, cc1c, cc2c, cc3c, cc1s, cc2s, cc3s;
	private String mvccI, crlI, pcrI, compSynI, bcI, 
		ctaI, mrI, cc1cI, cc2cI, cc3cI, cc1sI, cc2sI, cc3sI;
	//Battery Simple Properties and regular variables
	private SimpleStringProperty batv, cte, sbatv, lsc,
		ctd,ctc;
	private String batvI, cteI, sbatvI, lscI,
		ctdI,ctcI;
	//Loading Simple Properties and regular variables
	private SimpleStringProperty ctf,dcv,fwd,rev,spd,is,sixt,eightt,ninet,sixteent,
		tent,fivet,nsrc,twentthreet,rvr,nsrs,accel,wl,mvsr,mvsf,ahp,bhp,ideal,tmi,
		thp,grres,grco,ldsnd,gr,throttle;
	private String ctfI,dcvI,fwdI,revI,spdI,isI,sixtI,eighttI,ninetI,sixteentI,
		tentI,fivetI,nsrcI,twentthreetI,rvrI,nsrsI,accelI,wlI,mvsrI,mvsfI,ahpI,
		bhpI,idealI,tmiI,thpI,grresI,grcoI,ldsndI,grI,throttleI;
	//Traction motor Simple Properties and regular variables
	private SimpleStringProperty mfr,exclim,tmvone,tmione,tmvtwo,tmitwo,tmvthree,
		tmithree,tmvfour,tmifour,dutyone,dutytwo,dutythree,dutyfour,ponec,ptwoc,pthreec,
		pfourc,m,mcosone,mcostwo,mcosthree,mcosfour,mcoonec,mcotwoc,mcothreec,mcofourc,coneflt,
		conefai,ctwoflt,ctwofai,cthreeflt,cthreefai,cfourflt,cfourfai,pones,ptwos,pthrees,
		pfours,cprog,chcb,mtr,mcoones,mcotwos,mcothrees,mcofours;
	private String mfrI,exclimI,tmvoneI,tmioneI,tmvtwoI,tmitwoI,tmvthreeI,
	tmithreeI,tmvfourI,tmifourI,dutyoneI,dutytwoI,dutythreeI,dutyfourI,ponecI,ptwocI,pthreecI,
	pfourcI,mI,mcosoneI,mcostwoI,mcosthreeI,mcosfourI,mcoonecI,mcotwocI,mcothreecI,mcofourcI,conefltI,
	conefaiI,ctwofltI,ctwofaiI,cthreefltI,cthreefaiI,cfourfltI,cfourfaiI,ponesI,ptwosI,pthreesI,
	pfoursI,cprogI,chcbI,mtrI,mcoonesI,mcotwosI,mcothreesI,mcofoursI;
	//GS1 Simple Properties and regular variables
	private SimpleStringProperty gsaflt,gsasrv,gsabrk,gsaee,gsavro,gsast,gsapoi,gsapti,gsarpm,gsaoil,gsawat;
	private String gsafltI,gsasrvI,gsabrkI,gsaeeI,gsavroI,gsastI,gsapoiI,gsaptiI,gsarpmI,gsaoilI,gsawatI;
	//GS2 Simple Properties and regular variables
	private SimpleStringProperty gsbflt,gsbsrv,gsbbrk,gsbee,gsbvro,gsbst,gsbpoi,gsbpti,gsbrpm,gsboil,gsbwat;
	private String gsbfltI,gsbsrvI,gsbbrkI,gsbeeI,gsbvroI,gsbstI,gsbpoiI,gsbptiI,gsbrpmI,gsboilI,gsbwatI;	
	//GS3 Simple Properties and regular variables
	private SimpleStringProperty gscflt,gscsrv,gscbrk,gscee,gscvro,gscst,gscpoi,gscpti,gscrpm,gscoil,gscwat;
	private String gscfltI,gscsrvI,gscbrkI,gsceeI,gscvroI,gscstI,gscpoiI,gscptiI,gscrpmI,gscoilI,gscwatI;	
	//All GS Simple Properties and regular variables
	private SimpleStringProperty fpcr,ecmp,threet,sevent,twelvet,fifteent,jok,jact,dadt,rate;
	private String fpcrI,ecmpI,threetI,seventI,twelvetI,fifteentI,jokI,jactI,dadtI,rateI;
	//Auxiliary system Simple Properties and regular variables
	private SimpleStringProperty acv,auxfl,ctb,acconec,acctwoc,accthreec,accones,acctwos,accthrees,ebcc,ebcs;
	private String acvI,auxflI,ctbI,acconecI,acctwocI,accthreecI,acconesI,acctwosI,accthreesI,ebccI,ebcsI;
	//Miscellaneous system Simple Properties and regular variables
	private SimpleStringProperty acrst,buzz,lts,pa,rctot,twot,thirteent,start,twentysixt,
		lvflt,inflt,abr,shtrp,aats;
	private String acrstI,buzzI,ltsI,paI,rctotI,twotI,thirteentI,startI,twentysixtI,
		lvfltI,infltI,abrI,shtrpI,aatsI;
	
	
	public Data(String[] dataRow, int version) {
		//Get information for variables from row of data and convert to SimpleProperty
		String[] data = dataRow;
		String[] temp = dataRow[0].trim().split("/");
		if (temp.length == 3) {
			this.dayI = Integer.parseInt(temp[0]);
			this.day = new SimpleIntegerProperty(dayI);
			this.monthI = Integer.parseInt(temp[1]);
			this.month = new SimpleIntegerProperty(monthI);
			this.yearI = Integer.parseInt(temp[2]) + 2000;
			this.year = new SimpleIntegerProperty(yearI);
		}
		temp = dataRow[1].trim().split(":");
		if (temp.length == 3) {
			this.hourI = Integer.parseInt(temp[0]);
			this.hour = new SimpleIntegerProperty(hourI);
			this.minuteI = Integer.parseInt(temp[1]);
			this.minute = new SimpleIntegerProperty(minuteI);
			this.secondI = Float.parseFloat(temp[2]);
			this.second = new SimpleFloatProperty(secondI);
		}
		//Version 51 and lower has less column/variables
		if (version == 51 && dataRow.length == 168)  {
			mvccI = data[125];
			mvcc = new SimpleStringProperty(mvccI);
			crlI = data[119];
			crl = new SimpleStringProperty(crlI);
			pcrI = data[78];
			pcr = new SimpleStringProperty(pcrI);
			compSynI = data[81];
			compSyn = new SimpleStringProperty(compSynI);
			bcI = data[11];
			bc = new SimpleStringProperty(bcI);
			ctaI = data[18];
			cta = new SimpleStringProperty(ctaI);
			mrI = data[15];
			mr = new SimpleStringProperty(mrI);
			cc1cI = data[58];
			cc1c = new SimpleStringProperty(cc1cI);
			cc2cI = data[59];
			cc2c = new SimpleStringProperty(cc2cI);
			cc3cI = data[60];
			cc3c = new SimpleStringProperty(cc3cI);
			cc1sI = data[115];
			cc1s = new SimpleStringProperty(cc1sI);
			cc2sI = data[116];
			cc2s = new SimpleStringProperty(cc2sI);
			cc3sI = data[117];
			cc3s = new SimpleStringProperty(cc3sI);
			batvI = data[154];
			batv = new SimpleStringProperty(batvI);
			cteI = data[140];
			cte = new SimpleStringProperty(cteI);
			sbatvI = data[61];
			sbatv = new SimpleStringProperty(sbatvI);
			lscI = data[55];
			lsc = new SimpleStringProperty(lscI);
			ctdI = data[12];
			ctd = new SimpleStringProperty(ctdI);
			ctcI = data[16];
			ctc = new SimpleStringProperty(ctcI);
			ctfI = data[13];
			ctf = new SimpleStringProperty(ctfI);
			dcvI = data[14];
			dcv = new SimpleStringProperty(dcvI);
			fwdI = data[32];
			fwd = new SimpleStringProperty(fwdI);
			revI = data[33];
			rev = new SimpleStringProperty(revI);
			spdI = String.valueOf(Double.parseDouble(data[27])/10);
			spd = new SimpleStringProperty(spdI);
			isI = data[49];
			is = new SimpleStringProperty(isI);
			sixtI = data[63];
			sixt = new SimpleStringProperty(sixtI);
			eighttI = data[68];
			eightt = new SimpleStringProperty(eighttI);
			ninetI = data[69];
			ninet = new SimpleStringProperty(ninetI);
			sixteentI = data[70];
			sixteent = new SimpleStringProperty(sixteentI);
			tentI = data[73];
			tent = new SimpleStringProperty(tentI);
			fivetI = data[75];
			fivet = new SimpleStringProperty(fivetI);
			nsrcI = data[121];
			nsrc = new SimpleStringProperty(nsrcI);
			twentthreetI = data[77];
			twentthreet = new SimpleStringProperty(twentthreetI);
			rvrI = data[99];
			rvr = new SimpleStringProperty(rvrI);
			nsrsI = data[76];
			nsrs = new SimpleStringProperty(nsrsI);
			accelI = data[165];
			accel = new SimpleStringProperty(accelI);
			wlI = data[120];
			wl = new SimpleStringProperty(wlI);
			mvsrI = data[126];
			mvsr = new SimpleStringProperty(mvsrI);
			mvsfI = data[127];
			mvsf = new SimpleStringProperty(mvsfI);
			ahpI = data[156];
			ahp = new SimpleStringProperty(ahpI);
			bhpI = data[157];
			bhp = new SimpleStringProperty(bhpI);
			idealI = data[158];
			ideal = new SimpleStringProperty(idealI);
			tmiI = data[159];
			tmi = new SimpleStringProperty(tmiI);
			thpI = data[160];
			thp = new SimpleStringProperty(thpI);
			grresI = data[118];
			grres = new SimpleStringProperty(grresI);
			grcoI = data[56];
			grco = new SimpleStringProperty(grcoI);
			ldsndI = data[71];
			ldsnd = new SimpleStringProperty(ldsndI);
			grI = data[54];
			gr = new SimpleStringProperty(grI);
			throttleI = data[166];
			throttle = new SimpleStringProperty(throttleI);
			mfrI = data[123];
			mfr = new SimpleStringProperty(mfrI);
			exclimI = data[167];
			exclim = new SimpleStringProperty(exclimI);
			tmvoneI = data[9];
			tmvone = new SimpleStringProperty(tmvoneI);
			tmioneI = data[10];
			tmione = new SimpleStringProperty(tmioneI);
			tmvtwoI = data[7];
			tmvtwo = new SimpleStringProperty(tmvtwoI);
			tmitwoI = data[8];
			tmitwo = new SimpleStringProperty(tmitwoI);
			tmvthreeI = data[5];
			tmvthree = new SimpleStringProperty(tmvthreeI);
			tmithreeI = data[6];
			tmithree = new SimpleStringProperty(tmithreeI);
			tmvfourI = data[3];
			tmvfour = new SimpleStringProperty(tmvfourI);
			tmifourI = data[4];
			tmifour = new SimpleStringProperty(tmifourI);
			dutyoneI = data[23];
			dutyone = new SimpleStringProperty(dutyoneI);
			dutytwoI = data[24];
			dutytwo = new SimpleStringProperty(dutytwoI);
			dutythreeI = data[25];
			dutythree = new SimpleStringProperty(dutythreeI);
			dutyfourI = data[26];
			dutyfour = new SimpleStringProperty(dutyfourI);
			ponecI = data[94];
			ponec = new SimpleStringProperty(ponecI);
			ptwocI = data[95];
			ptwoc = new SimpleStringProperty(ptwocI);
			pthreecI = data[96];
			pthreec = new SimpleStringProperty(pthreecI);
			pfourcI = data[97];
			pfourc = new SimpleStringProperty(pfourcI);
			mI = data[34];
			m = new SimpleStringProperty(mI);
			mcosoneI = data[37];
			mcosone = new SimpleStringProperty(mcosoneI);
			mcostwoI = data[38];
			mcostwo = new SimpleStringProperty(mcostwoI);
			mcosthreeI = data[39];
			mcosthree = new SimpleStringProperty(mcosthreeI);
			mcosfourI = data[40];
			mcosfour = new SimpleStringProperty(mcosfourI);
			mcoonecI = data[102];
			mcoonec = new SimpleStringProperty(mcoonecI);
			mcotwocI = data[103];
			mcotwoc = new SimpleStringProperty(mcotwocI);
			mcothreecI = data[104];
			mcothreec = new SimpleStringProperty(mcothreecI);
			mcofourcI = data[101];
			mcofourc = new SimpleStringProperty(mcofourcI);
			conefltI = data[84];
			coneflt = new SimpleStringProperty(conefltI);
			conefaiI = data[83];
			conefai = new SimpleStringProperty(conefaiI);
			ctwofltI = data[88];
			ctwoflt = new SimpleStringProperty(ctwofltI);
			ctwofaiI = data[87];
			ctwofai = new SimpleStringProperty(ctwofaiI);
			cthreefltI = data[90];
			cthreeflt = new SimpleStringProperty(cthreefltI);
			cthreefaiI = data[89];
			cthreefai = new SimpleStringProperty(cthreefaiI);
			cfourfltI = data[92];
			cfourflt = new SimpleStringProperty(cfourfltI);
			cfourfaiI = data[91];
			cfourfai = new SimpleStringProperty(cfourfaiI);
			ponesI = data[28];
			pones = new SimpleStringProperty(ponesI);
			ptwosI = data[29];
			ptwos = new SimpleStringProperty(ptwosI);
			pthreesI = data[30];
			pthrees = new SimpleStringProperty(pthreesI);
			pfoursI = data[31];
			pfours = new SimpleStringProperty(pfoursI);
			cprogI = data[93];
			cprog = new SimpleStringProperty(cprogI);
			chcbI = data[82];
			chcb = new SimpleStringProperty(chcbI);
			mtrI = data[100];
			mtr = new SimpleStringProperty(mtrI);
			mcoonesI = data[50];
			mcoones = new SimpleStringProperty(mcoonesI);
			mcotwosI = data[51];
			mcotwos = new SimpleStringProperty(mcotwosI);
			mcothreesI = data[52];
			mcothrees = new SimpleStringProperty(mcothreesI);
			mcofoursI = data[53];
			mcofours = new SimpleStringProperty(mcofoursI);
			gsafltI = data[106];
			gsaflt = new SimpleStringProperty(gsafltI);
			gsasrvI = data[107];
			gsasrv = new SimpleStringProperty(gsasrvI);
			gsabrkI = data[45];
			gsabrk = new SimpleStringProperty(gsabrkI);
			gsaeeI = data[128];
			gsaee = new SimpleStringProperty(gsaeeI);
			gsavroI = data[129];
			gsavro = new SimpleStringProperty(gsavroI);
			gsastI = data[130];
			gsast = new SimpleStringProperty(gsastI);
			gsapoiI = data[143];
			gsapoi = new SimpleStringProperty(gsapoiI);
			gsaptiI = data[141];
			gsapti = new SimpleStringProperty(gsaptiI);
			gsarpmI = data[148];
			gsarpm = new SimpleStringProperty(gsarpmI);
			gsaoilI = data[152];
			gsaoil = new SimpleStringProperty(gsaoilI);
			gsawatI = data[161];
			gsawat = new SimpleStringProperty(gsawatI);
			gsbfltI = data[109];
			gsbflt = new SimpleStringProperty(gsbfltI);
			gsbsrvI = data[110];
			gsbsrv = new SimpleStringProperty(gsbsrvI);
			gsbbrkI = data[46];
			gsbbrk = new SimpleStringProperty(gsbbrkI);
			gsbeeI = data[133];
			gsbee = new SimpleStringProperty(gsbeeI);
			gsbvroI = data[134];
			gsbvro = new SimpleStringProperty(gsbvroI);
			gsbstI = data[135];
			gsbst = new SimpleStringProperty(gsbstI);
			gsbpoiI = data[145];
			gsbpoi = new SimpleStringProperty(gsbpoiI);
			gsbptiI = data[147];
			gsbpti = new SimpleStringProperty(gsbptiI);
			gsbrpmI = data[149];
			gsbrpm = new SimpleStringProperty(gsbrpmI);
			gsboilI = data[153];
			gsboil = new SimpleStringProperty(gsboilI);
			gsbwatI = data[162];
			gsbwat = new SimpleStringProperty(gsbwatI);
			gscfltI = data[108];
			gscflt = new SimpleStringProperty(gscfltI);
			gscsrvI = data[111];
			gscsrv = new SimpleStringProperty(gscsrvI);
			gscbrkI = data[47];
			gscbrk = new SimpleStringProperty(gscbrkI);
			gsceeI = data[137];
			gscee = new SimpleStringProperty(gsceeI);
			gscvroI = data[138];
			gscvro = new SimpleStringProperty(gscvroI);
			gscstI = data[139];
			gscst = new SimpleStringProperty(gscstI);
			gscpoiI = data[142];
			gscpoi = new SimpleStringProperty(gscpoiI);
			gscptiI = data[144];
			gscpti = new SimpleStringProperty(gscptiI);
			gscrpmI = data[150];
			gscrpm = new SimpleStringProperty(gscrpmI);
			gscoilI = data[155];
			gscoil = new SimpleStringProperty(gscoilI);
			gscwatI = data[163];
			gscwat = new SimpleStringProperty(gscwatI);
			fpcrI = data[35];
			fpcr = new SimpleStringProperty(fpcrI);
			ecmpI = data[132];
			ecmp = new SimpleStringProperty(ecmpI);
			threetI = data[64];
			threet = new SimpleStringProperty(threetI);
			seventI = data[65];
			sevent = new SimpleStringProperty(seventI);
			twelvetI = data[66];
			twelvet = new SimpleStringProperty(twelvetI);
			fifteentI = data[67];
			fifteent = new SimpleStringProperty(fifteentI);
			jokI = data[19];
			jok = new SimpleStringProperty(jokI);
			jactI = data[20];
			jact = new SimpleStringProperty(jactI);
			dadtI = data[21];
			dadt = new SimpleStringProperty(dadtI);
			rateI = data[22];
			rate = new SimpleStringProperty(rateI);
			acvI = data[151];
			acv = new SimpleStringProperty(acvI);
			auxflI = data[124];
			auxfl = new SimpleStringProperty(auxflI);
			ctbI = data[17];
			ctb = new SimpleStringProperty(ctbI);
			acconecI = data[112];
			acconec = new SimpleStringProperty(acconecI);
			acctwocI = data[113];
			acctwoc = new SimpleStringProperty(acctwocI);
			accthreecI = data[98];
			accthreec = new SimpleStringProperty(accthreecI);
			acconesI = data[41];
			accones = new SimpleStringProperty(acconesI);
			acctwosI = data[42];
			acctwos = new SimpleStringProperty(acctwosI);
			accthreesI = data[43];
			accthrees = new SimpleStringProperty(accthreesI);
			ebccI = data[114];
			ebcc = new SimpleStringProperty(ebccI);
			ebcsI = data[57];
			ebcs = new SimpleStringProperty(ebcsI);
			acrstI = data[62];
			acrst = new SimpleStringProperty(acrstI);
			buzzI = data[105];
			buzz = new SimpleStringProperty(buzzI);
			ltsI = data[36];
			lts = new SimpleStringProperty(ltsI);
			paI = data[44];
			pa = new SimpleStringProperty(paI);
			rctotI = data[48];
			rctot = new SimpleStringProperty(rctotI);
			twotI = data[72];
			twot = new SimpleStringProperty(twotI);
			thirteentI = data[74];
			thirteent = new SimpleStringProperty(thirteentI);
			startI = data[79];
			start = new SimpleStringProperty(startI);
			twentysixtI = data[80];
			twentysixt = new SimpleStringProperty(twentysixtI);
			lvfltI = data[85];
			lvflt = new SimpleStringProperty(lvfltI);
			infltI = data[86];
			inflt = new SimpleStringProperty(infltI);
			abrI = data[122];
			abr = new SimpleStringProperty(abrI);
			shtrpI = data[131];
			shtrp = new SimpleStringProperty(shtrpI);
			aatsI = data[146];
			aats = new SimpleStringProperty(aatsI);
		} else if (version == 53 && dataRow.length == 172)  {
			//Version 53 and higher has more column/variables
			mvccI = data[129];
			mvcc = new SimpleStringProperty(mvccI);
			crlI = data[123];
			crl = new SimpleStringProperty(crlI);
			pcrI = data[82];
			pcr = new SimpleStringProperty(pcrI);
			compSynI = data[85];
			compSyn = new SimpleStringProperty(compSynI);
			bcI = data[11];
			bc = new SimpleStringProperty(bcI);
			ctaI = data[18];
			cta = new SimpleStringProperty(ctaI);
			mrI = data[15];
			mr = new SimpleStringProperty(mrI);
			cc1cI = data[62];
			cc1c = new SimpleStringProperty(cc1cI);
			cc2cI = data[63];
			cc2c = new SimpleStringProperty(cc2cI);
			cc3cI = data[64];
			cc3c = new SimpleStringProperty(cc3cI);
			cc1sI = data[119];
			cc1s = new SimpleStringProperty(cc1sI);
			cc2sI = data[120];
			cc2s = new SimpleStringProperty(cc2sI);
			cc3sI = data[121];
			cc3s = new SimpleStringProperty(cc2sI);
			batvI = data[158];
			batv = new SimpleStringProperty(batvI);
			cteI = data[144];
			cte = new SimpleStringProperty(cteI);
			sbatvI = data[65];
			sbatv = new SimpleStringProperty(sbatvI);
			lscI = data[59];
			lsc = new SimpleStringProperty(lscI);
			ctdI = data[12];
			ctd = new SimpleStringProperty(ctdI);
			ctcI = data[16];
			ctc = new SimpleStringProperty(ctcI);
			ctfI = data[13];
			ctf = new SimpleStringProperty(ctfI);
			dcvI = data[14];
			dcv = new SimpleStringProperty(dcvI);
			fwdI = data[34];
			fwd = new SimpleStringProperty(fwdI);
			revI = data[35];
			rev = new SimpleStringProperty(revI);
			spdI = String.valueOf(Double.parseDouble(data[29])/10);
			spd = new SimpleStringProperty(spdI);
			isI = data[53];
			is = new SimpleStringProperty(isI);
			sixtI = data[67];
			sixt = new SimpleStringProperty(sixtI);
			eighttI = data[72];
			eightt = new SimpleStringProperty(eighttI);
			ninetI = data[73];
			ninet = new SimpleStringProperty(ninetI);
			sixteentI = data[74];
			sixteent = new SimpleStringProperty(sixteentI);
			tentI = data[77];
			tent = new SimpleStringProperty(tentI);
			fivetI = data[79];
			fivet = new SimpleStringProperty(fivetI);
			nsrcI = data[125];
			nsrc = new SimpleStringProperty(nsrcI);
			twentthreetI = data[81];
			twentthreet = new SimpleStringProperty(twentthreetI);
			rvrI = data[103];
			rvr = new SimpleStringProperty(rvrI);
			nsrsI = data[80];
			nsrs = new SimpleStringProperty(nsrsI);
			accelI = data[169];
			accel = new SimpleStringProperty(accelI);
			wlI = data[124];
			wl = new SimpleStringProperty(wlI);
			mvsrI = data[130];
			mvsr = new SimpleStringProperty(mvsrI);
			mvsfI = data[131];
			mvsf = new SimpleStringProperty(mvsfI);
			ahpI = data[160];
			ahp = new SimpleStringProperty(ahpI);
			bhpI = data[161];
			bhp = new SimpleStringProperty(bhpI);
			idealI = data[162];
			ideal = new SimpleStringProperty(idealI);
			tmiI = data[163];
			tmi = new SimpleStringProperty(tmiI);
			thpI = data[164];
			thp = new SimpleStringProperty(thpI);
			grresI = data[122];
			grres = new SimpleStringProperty(grresI);
			grcoI = data[60];
			grco = new SimpleStringProperty(grcoI);
			ldsndI = data[75];
			ldsnd = new SimpleStringProperty(ldsndI);
			grI = data[58];
			gr = new SimpleStringProperty(grI);
			throttleI = data[170];
			throttle = new SimpleStringProperty(throttleI);
			mfrI = data[127];
			mfr = new SimpleStringProperty(mfrI);
			exclimI = data[171];
			exclim = new SimpleStringProperty(exclimI);
			tmvoneI = data[9];
			tmvone = new SimpleStringProperty(tmvoneI);
			tmioneI = data[10];
			tmione = new SimpleStringProperty(tmioneI);
			tmvtwoI = data[7];
			tmvtwo = new SimpleStringProperty(tmvtwoI);
			tmitwoI = data[8];
			tmitwo = new SimpleStringProperty(tmitwoI);
			tmvthreeI = data[5];
			tmvthree = new SimpleStringProperty(tmvthreeI);
			tmithreeI = data[6];
			tmithree = new SimpleStringProperty(tmithreeI);
			tmvfourI = data[3];
			tmvfour = new SimpleStringProperty(tmvfourI);
			tmifourI = data[4];
			tmifour = new SimpleStringProperty(tmifourI);
			dutyoneI = data[25];
			dutyone = new SimpleStringProperty(dutyoneI);
			dutytwoI = data[26];
			dutytwo = new SimpleStringProperty(dutytwoI);
			dutythreeI = data[27];
			dutythree = new SimpleStringProperty(dutythreeI);
			dutyfourI = data[28];
			dutyfour = new SimpleStringProperty(dutyfourI);
			ponecI = data[98];
			ponec = new SimpleStringProperty(ponecI);
			ptwocI = data[99];
			ptwoc = new SimpleStringProperty(ptwocI);
			pthreecI = data[100];
			pthreec = new SimpleStringProperty(pthreecI);
			pfourcI = data[101];
			pfourc = new SimpleStringProperty(pfourcI);
			mI = data[38];
			m = new SimpleStringProperty(mI);
			mcosoneI = data[41];
			mcosone = new SimpleStringProperty(mcosoneI);
			mcostwoI = data[42];
			mcostwo = new SimpleStringProperty(mcostwoI);
			mcosthreeI = data[43];
			mcosthree = new SimpleStringProperty(mcosthreeI);
			mcosfourI = data[44];
			mcosfour = new SimpleStringProperty(mcosfourI);
			mcoonecI = data[106];
			mcoonec = new SimpleStringProperty(mcoonecI);
			mcotwocI = data[107];
			mcotwoc = new SimpleStringProperty(mcotwocI);
			mcothreecI = data[108];
			mcothreec = new SimpleStringProperty(mcothreecI);
			mcofourcI = data[105];
			mcofourc = new SimpleStringProperty(mcofourcI);
			conefltI = data[88];
			coneflt = new SimpleStringProperty(conefltI);
			conefaiI = data[87];
			conefai = new SimpleStringProperty(conefaiI);
			ctwofltI = data[92];
			ctwoflt = new SimpleStringProperty(ctwofltI);
			ctwofaiI = data[91];
			ctwofai = new SimpleStringProperty(ctwofaiI);
			cthreefltI = data[94];
			cthreeflt = new SimpleStringProperty(cthreefltI);
			cthreefaiI = data[93];
			cthreefai = new SimpleStringProperty(cthreefaiI);
			cfourfltI = data[96];
			cfourflt = new SimpleStringProperty(cfourfltI);
			cfourfaiI = data[95];
			cfourfai = new SimpleStringProperty(cfourfaiI);
			ponesI = data[30];
			pones = new SimpleStringProperty(ponesI);
			ptwosI = data[31];
			ptwos = new SimpleStringProperty(ptwosI);
			pthreesI = data[32];
			pthrees = new SimpleStringProperty(pthreesI);
			pfoursI = data[33];
			pfours = new SimpleStringProperty(pfoursI);
			cprogI = data[97];
			cprog = new SimpleStringProperty(cprogI);
			chcbI = data[86];
			chcb = new SimpleStringProperty(chcbI);
			mtrI = data[104];
			mtr = new SimpleStringProperty(mtrI);
			mcoonesI = data[54];
			mcoones = new SimpleStringProperty(mcoonesI);
			mcotwosI = data[55];
			mcotwos = new SimpleStringProperty(mcotwosI);
			mcothreesI = data[56];
			mcothrees = new SimpleStringProperty(mcothreesI);
			mcofoursI = data[57];
			mcofours = new SimpleStringProperty(mcofoursI);
			gsafltI = data[110];
			gsaflt = new SimpleStringProperty(gsafltI);
			gsasrvI = data[111];
			gsasrv = new SimpleStringProperty(gsasrvI);
			gsabrkI = data[49];
			gsabrk = new SimpleStringProperty(gsabrkI);
			gsaeeI = data[132];
			gsaee = new SimpleStringProperty(gsaeeI);
			gsavroI = data[133];
			gsavro = new SimpleStringProperty(gsavroI);
			gsastI = data[134];
			gsast = new SimpleStringProperty(gsastI);
			gsapoiI = data[147];
			gsapoi = new SimpleStringProperty(gsapoiI);
			gsaptiI = data[145];
			gsapti = new SimpleStringProperty(gsaptiI);
			gsarpmI = data[152];
			gsarpm = new SimpleStringProperty(gsarpmI);
			gsaoilI = data[156];
			gsaoil = new SimpleStringProperty(gsaoilI);
			gsawatI = data[165];
			gsawat = new SimpleStringProperty(gsawatI);
			gsbfltI = data[113];
			gsbflt = new SimpleStringProperty(gsbfltI);
			gsbsrvI = data[114];
			gsbsrv = new SimpleStringProperty(gsbsrvI);
			gsbbrkI = data[50];
			gsbbrk = new SimpleStringProperty(gsbbrkI);
			gsbeeI = data[137];
			gsbee = new SimpleStringProperty(gsbeeI);
			gsbvroI = data[138];
			gsbvro = new SimpleStringProperty(gsbvroI);
			gsbstI = data[139];
			gsbst = new SimpleStringProperty(gsbstI);
			gsbpoiI = data[149];
			gsbpoi = new SimpleStringProperty(gsbpoiI);
			gsbptiI = data[151];
			gsbpti = new SimpleStringProperty(gsbptiI);
			gsbrpmI = data[153];
			gsbrpm = new SimpleStringProperty(gsbrpmI);
			gsboilI = data[157];
			gsboil = new SimpleStringProperty(gsboilI);
			gsbwatI = data[166];
			gsbwat = new SimpleStringProperty(gsbwatI);
			gscfltI = data[112];
			gscflt = new SimpleStringProperty(gscfltI);
			gscsrvI = data[115];
			gscsrv = new SimpleStringProperty(gscsrvI);
			gscbrkI = data[51];
			gscbrk = new SimpleStringProperty(gscbrkI);
			gsceeI = data[141];
			gscee = new SimpleStringProperty(gsceeI);
			gscvroI = data[142];
			gscvro = new SimpleStringProperty(gscvroI);
			gscstI = data[143];
			gscst = new SimpleStringProperty(gscstI);
			gscpoiI = data[146];
			gscpoi = new SimpleStringProperty(gscpoiI);
			gscptiI = data[148];
			gscpti = new SimpleStringProperty(gscptiI);
			gscrpmI = data[154];
			gscrpm = new SimpleStringProperty(gscrpmI);
			gscoilI = data[159];
			gscoil = new SimpleStringProperty(gscoilI);
			gscwatI = data[167];
			gscwat = new SimpleStringProperty(gscwatI);
			fpcrI = data[39];
			fpcr = new SimpleStringProperty(fpcrI);
			ecmpI = data[136];
			ecmp = new SimpleStringProperty(ecmpI);
			threetI = data[68];
			threet = new SimpleStringProperty(threetI);
			seventI = data[69];
			sevent = new SimpleStringProperty(seventI);
			twelvetI = data[70];
			twelvet = new SimpleStringProperty(twelvetI);
			fifteentI = data[71];
			fifteent = new SimpleStringProperty(fifteentI);
			jokI = data[19];
			jok = new SimpleStringProperty(jokI);
			jactI = data[20];
			jact = new SimpleStringProperty(jactI);
			dadtI = data[21];
			dadt = new SimpleStringProperty(dadtI);
			rateI = data[22];
			rate = new SimpleStringProperty(rateI);
			acvI = data[155];
			acv = new SimpleStringProperty(acvI);
			auxflI = data[128];
			auxfl = new SimpleStringProperty(auxflI);
			ctbI = data[17];
			ctb = new SimpleStringProperty(ctbI);
			acconecI = data[116];
			acconec = new SimpleStringProperty(acconecI);
			acctwocI = data[117];
			acctwoc = new SimpleStringProperty(acctwocI);
			accthreecI = data[102];
			accthreec = new SimpleStringProperty(accthreecI);
			acconesI = data[45];
			accones = new SimpleStringProperty(acconesI);
			acctwosI = data[46];
			acctwos = new SimpleStringProperty(acctwosI);
			accthreesI = data[47];
			accthrees = new SimpleStringProperty(accthreesI);
			ebccI = data[118];
			ebcc = new SimpleStringProperty(ebccI);
			ebcsI = data[61];
			ebcs = new SimpleStringProperty(ebcsI);
			acrstI = data[66];
			acrst = new SimpleStringProperty(acrstI);
			buzzI = data[109];
			buzz = new SimpleStringProperty(buzzI);
			ltsI = data[40];
			lts = new SimpleStringProperty(ltsI);
			paI = data[48];
			pa = new SimpleStringProperty(paI);
			rctotI = data[52];
			rctot = new SimpleStringProperty(rctotI);
			twotI = data[76];
			twot = new SimpleStringProperty(twotI);
			thirteentI = data[78];
			thirteent = new SimpleStringProperty(thirteentI);
			startI = data[83];
			start = new SimpleStringProperty(startI);
			twentysixtI = data[84];
			twentysixt = new SimpleStringProperty(twentysixtI);
			lvfltI = data[89];
			lvflt = new SimpleStringProperty(lvfltI);
			infltI = data[90];
			inflt = new SimpleStringProperty(infltI);
			abrI = data[126];
			abr = new SimpleStringProperty(abrI);
			shtrpI = data[135];
			shtrp = new SimpleStringProperty(shtrpI);
			aatsI = data[150];
			aats = new SimpleStringProperty(aatsI);
		} else {
			SimpleStringProperty blanker = new SimpleStringProperty("");
			SimpleStringProperty faultHolder = new SimpleStringProperty(data[2]);
			mvcc = blanker;
			crl = blanker;
			pcr = blanker;
			compSyn = blanker;
			bc = faultHolder;
			cta = blanker;
			mr = blanker;
			cc1c = blanker;
			cc2c = blanker;
			cc3c = blanker;
			cc1s = blanker;
			cc2s = blanker;
			cc3s = blanker;
			batv = faultHolder;
			cte = blanker;
			sbatv = blanker;
			lsc = blanker;
			ctd = blanker;
			ctc = blanker;
			ctf = blanker;
			dcv = blanker;
			fwd = blanker;
			rev = blanker;
			spd = blanker;
			is = blanker;
			sixt = blanker;
			eightt = blanker;
			ninet = blanker;
			sixteent = blanker;
			tent = blanker;
			fivet = blanker;
			nsrc = blanker;
			twentthreet = blanker;
			rvr = blanker;
			nsrs = blanker;
			accel = blanker;
			wl = blanker;
			mvsr = blanker;
			mvsf = blanker;
			ahp = blanker;
			bhp = blanker;
			ideal = blanker;
			tmi = faultHolder;
			thp = blanker;
			grres = blanker;
			grco = blanker;
			ldsnd = blanker;
			gr = blanker;
			throttle = faultHolder;
			mfr = blanker;
			exclim = blanker;
			tmvone = blanker;
			tmione = blanker;
			tmvtwo = blanker;
			tmitwo = blanker;
			tmvthree = blanker;
			tmithree = blanker;
			tmvfour = blanker;
			tmifour = blanker;
			dutyone = blanker;
			dutytwo = blanker;
			dutythree = blanker;
			dutyfour = blanker;
			ponec = blanker;
			ptwoc = blanker;
			pthreec = blanker;
			pfourc = blanker;
			m = blanker;
			mcosone = blanker;
			mcostwo = blanker;
			mcosthree = blanker;
			mcosfour = blanker;
			mcoonec = blanker;
			mcotwoc = blanker;
			mcothreec = blanker;
			mcofourc = blanker;
			coneflt = blanker;
			conefai = blanker;
			ctwoflt = blanker;
			ctwofai = blanker;
			cthreeflt = blanker;
			cthreefai = blanker;
			cfourflt = blanker;
			cfourfai = blanker;
			pones = blanker;
			ptwos = blanker;
			pthrees = blanker;
			pfours = blanker;
			cprog = blanker;
			chcb = blanker;
			mtr = blanker;
			mcoones = blanker;
			mcotwos = blanker;
			mcothrees = blanker;
			mcofours = blanker;
			gsaflt = blanker;
			gsasrv = blanker;
			gsabrk = blanker;
			gsaee = blanker;
			gsavro = blanker;
			gsast = blanker;
			gsapoi = blanker;
			gsapti = blanker;
			gsarpm = faultHolder;
			gsaoil = blanker;
			gsawat = blanker;
			gsbflt = blanker;
			gsbsrv = blanker;
			gsbbrk = blanker;
			gsbee = blanker;
			gsbvro = blanker;
			gsbst = blanker;
			gsbpoi = blanker;
			gsbpti = blanker;
			gsbrpm = faultHolder;
			gsboil = blanker;
			gsbwat = blanker;
			gscflt = blanker;
			gscsrv = blanker;
			gscbrk = blanker;
			gscee = blanker;
			gscvro = blanker;
			gscst = blanker;
			gscpoi = blanker;
			gscpti = blanker;
			gscrpm = faultHolder;
			gscoil = blanker;
			gscwat = blanker;
			fpcr = blanker;
			ecmp = blanker;
			threet = blanker;
			sevent = blanker;
			twelvet = blanker;
			fifteent = blanker;
			jok = blanker;
			jact = blanker;
			dadt = blanker;
			rate = blanker;
			acv = faultHolder;
			auxfl = blanker;
			ctb = blanker;
			acconec = blanker;
			acctwoc = blanker;
			accthreec = blanker;
			accones = blanker;
			acctwos = blanker;
			accthrees = blanker;
			ebcc = blanker;
			ebcs = blanker;
			acrst = blanker;
			buzz = blanker;
			lts = blanker;
			pa = blanker;
			rctot = blanker;
			twot = blanker;
			thirteent = blanker;
			start = faultHolder;
			twentysixt = blanker;
			lvflt = blanker;
			inflt = blanker;
			abr = blanker;
			shtrp = blanker;
			aats = blanker;
		}
	}

	public int getYear() {
		return year.get();
	}

	public int getMonth() {
		return month.get();
	}

	public int getDay() {
		return day.get();
	}
	
	public int getHour() {
		return hour.get();
	}

	public int getMinute() {
		return minute.get();
	}

	public float getSecond() {
		return second.get();
	}
	
	public String getDate() {
		date = new SimpleStringProperty(getMonth() + "/" + getDay() + "/" + getYear());
		return date.get();
	}
	
	public String getTime() {
		time = new SimpleStringProperty(getHour() + ":" + getMinute() + ":" + getSecond());
		return time.get();
	}	

	public String getMvcc() {
		return mvcc.get();
	}

	public String getCrl() {
		return crl.get();
	}

	public String getPcr() {
		return pcr.get();
	}

	public String getCompSyn() {
		return compSyn.get();
	}

	public String getBc() {
		return bc.get();
	}

	public String getCta() {
		return cta.get();
	}

	public String getMr() {
		return mr.get();
	}

	public String getCc1c() {
		return cc1c.get();
	}

	public String getCc2c() {
		return cc2c.get();
	}

	public String getCc3c() {
		return cc3c.get();
	}

	public String getCc1s() {
		return cc1s.get();
	}

	public String getCc2s() {
		return cc2s.get();
	}

	public String getCc3s() {
		return cc3s.get();
	}

	public String getBatv() {
		return batv.get();
	}

	public String getCte() {
		return cte.get();
	}

	public String getSbatv() {
		return sbatv.get();
	}

	public String getLsc() {
		return lsc.get();
	}

	public String getCtd() {
		return ctd.get();
	}

	public String getCtc() {
		return ctc.get();
	}

	public String getCtf() {
		return ctf.get();
	}

	public String getDcv() {
		return dcv.get();
	}

	public String getFwd() {
		return fwd.get();
	}

	public String getRev() {
		return rev.get();
	}

	public String getSpd() {
		return spd.get();
	}

	public String getIs() {
		return is.get();
	}

	public String getSixt() {
		return sixt.get();
	}

	public String getEightt() {
		return eightt.get();
	}

	public String getNinet() {
		return ninet.get();
	}

	public String getSixteent() {
		return sixteent.get();
	}

	public String getTent() {
		return tent.get();
	}

	public String getFivet() {
		return fivet.get();
	}

	public String getNsrc() {
		return nsrc.get();
	}

	public String getTwentthreet() {
		return twentthreet.get();
	}

	public String getRvr() {
		return rvr.get();
	}

	public String getNsrs() {
		return nsrs.get();
	}

	public String getAccel() {
		return accel.get();
	}

	public String getWl() {
		return wl.get();
	}

	public String getMvsr() {
		return mvsr.get();
	}

	public String getMvsf() {
		return mvsf.get();
	}

	public String getAhp() {
		return ahp.get();
	}

	public String getBhp() {
		return bhp.get();
	}

	public String getIdeal() {
		return ideal.get();
	}

	public String getTmi() {
		return tmi.get();
	}

	public String getThp() {
		return thp.get();
	}

	public String getGrres() {
		return grres.get();
	}

	public String getGrco() {
		return grco.get();
	}

	public String getLdsnd() {
		return ldsnd.get();
	}

	public String getGr() {
		return gr.get();
	}

	public String getThrottle() {
		return throttle.get();
	}

	public String getMfr() {
		return mfr.get();
	}

	public String getExclim() {
		return exclim.get();
	}

	public String getTmvone() {
		return tmvone.get();
	}

	public String getTmione() {
		return tmione.get();
	}

	public String getTmvtwo() {
		return tmvtwo.get();
	}

	public String getTmitwo() {
		return tmitwo.get();
	}

	public String getTmvthree() {
		return tmvthree.get();
	}

	public String getTmithree() {
		return tmithree.get();
	}

	public String getTmvfour() {
		return tmvfour.get();
	}

	public String getTmifour() {
		return tmifour.get();
	}

	public String getDutyone() {
		return dutyone.get();
	}

	public String getDutytwo() {
		return dutytwo.get();
	}

	public String getDutythree() {
		return dutythree.get();
	}

	public String getDutyfour() {
		return dutyfour.get();
	}

	public String getPonec() {
		return ponec.get();
	}

	public String getPtwoc() {
		return ptwoc.get();
	}

	public String getPthreec() {
		return pthreec.get();
	}

	public String getPfourc() {
		return pfourc.get();
	}

	public String getM() {
		return m.get();
	}

	public String getMcosone() {
		return mcosone.get();
	}

	public String getMcostwo() {
		return mcostwo.get();
	}

	public String getMcosthree() {
		return mcosthree.get();
	}

	public String getMcosfour() {
		return mcosfour.get();
	}

	public String getMcoonec() {
		return mcoonec.get();
	}

	public String getMcotwoc() {
		return mcotwoc.get();
	}

	public String getMcothreec() {
		return mcothreec.get();
	}

	public String getMcofourc() {
		return mcofourc.get();
	}

	public String getConeflt() {
		return coneflt.get();
	}

	public String getConefai() {
		return conefai.get();
	}

	public String getCtwoflt() {
		return ctwoflt.get();
	}

	public String getCtwofai() {
		return ctwofai.get();
	}

	public String getCthreeflt() {
		return cthreeflt.get();
	}

	public String getCthreefai() {
		return cthreefai.get();
	}

	public String getCfourflt() {
		return cfourflt.get();
	}

	public String getCfourfai() {
		return cfourfai.get();
	}

	public String getPones() {
		return pones.get();
	}

	public String getPtwos() {
		return ptwos.get();
	}

	public String getPthrees() {
		return pthrees.get();
	}

	public String getPfours() {
		return pfours.get();
	}

	public String getCprog() {
		return cprog.get();
	}

	public String getChcb() {
		return chcb.get();
	}

	public String getMtr() {
		return mtr.get();
	}

	public String getMcoones() {
		return mcoones.get();
	}

	public String getMcotwos() {
		return mcotwos.get();
	}

	public String getMcothrees() {
		return mcothrees.get();
	}

	public String getMcofours() {
		return mcofours.get();
	}

	public String getGsaflt() {
		return gsaflt.get();
	}

	public String getGsasrv() {
		return gsasrv.get();
	}

	public String getGsabrk() {
		return gsabrk.get();
	}

	public String getGsaee() {
		return gsaee.get();
	}

	public String getGsavro() {
		return gsavro.get();
	}

	public String getGsast() {
		return gsast.get();
	}

	public String getGsapoi() {
		return gsapoi.get();
	}

	public String getGsapti() {
		return gsapti.get();
	}

	public String getGsarpm() {
		return gsarpm.get();
	}

	public String getGsaoil() {
		return gsaoil.get();
	}

	public String getGsawat() {
		return gsawat.get();
	}

	public String getGsbflt() {
		return gsbflt.get();
	}

	public String getGsbsrv() {
		return gsbsrv.get();
	}

	public String getGsbbrk() {
		return gsbbrk.get();
	}

	public String getGsbee() {
		return gsbee.get();
	}

	public String getGsbvro() {
		return gsbvro.get();
	}

	public String getGsbst() {
		return gsbst.get();
	}

	public String getGsbpoi() {
		return gsbpoi.get();
	}

	public String getGsbpti() {
		return gsbpti.get();
	}

	public String getGsbrpm() {
		return gsbrpm.get();
	}

	public String getGsboil() {
		return gsboil.get();
	}

	public String getGsbwat() {
		return gsbwat.get();
	}

	public String getGscflt() {
		return gscflt.get();
	}

	public String getGscsrv() {
		return gscsrv.get();
	}

	public String getGscbrk() {
		return gscbrk.get();
	}

	public String getGscee() {
		return gscee.get();
	}

	public String getGscvro() {
		return gscvro.get();
	}

	public String getGscst() {
		return gscst.get();
	}

	public String getGscpoi() {
		return gscpoi.get();
	}

	public String getGscpti() {
		return gscpti.get();
	}

	public String getGscrpm() {
		return gscrpm.get();
	}

	public String getGscoil() {
		return gscoil.get();
	}

	public String getGscwat() {
		return gscwat.get();
	}

	public String getFpcr() {
		return fpcr.get();
	}

	public String getEcmp() {
		return ecmp.get();
	}

	public String getThreet() {
		return threet.get();
	}

	public String getSevent() {
		return sevent.get();
	}

	public String getTwelvet() {
		return twelvet.get();
	}

	public String getFifteent() {
		return fifteent.get();
	}

	public String getJok() {
		return jok.get();
	}

	public String getJact() {
		return jact.get();
	}

	public String getDadt() {
		return dadt.get();
	}

	public String getRate() {
		return rate.get();
	}

	public String getAcv() {
		return acv.get();
	}

	public String getAuxfl() {
		return auxfl.get();
	}

	public String getCtb() {
		return ctb.get();
	}

	public String getAcconec() {
		return acconec.get();
	}

	public String getAcctwoc() {
		return acctwoc.get();
	}

	public String getAccthreec() {
		return accthreec.get();
	}

	public String getAccones() {
		return accones.get();
	}

	public String getAcctwos() {
		return acctwos.get();
	}

	public String getAccthrees() {
		return accthrees.get();
	}

	public String getEbcc() {
		return ebcc.get();
	}

	public String getEbcs() {
		return ebcs.get();
	}

	public String getAcrst() {
		return acrst.get();
	}

	public String getBuzz() {
		return buzz.get();
	}

	public String getLts() {
		return lts.get();
	}

	public String getPa() {
		return pa.get();
	}

	public String getRctot() {
		return rctot.get();
	}

	public String getTwot() {
		return twot.get();
	}

	public String getThirteent() {
		return thirteent.get();
	}

	public String getStart() {
		return start.get();
	}

	public String getTwentysixt() {
		return twentysixt.get();
	}
	
	public String getLvflt() {
		return lvflt.get();
	}

	public String getInflt() {
		return inflt.get();
	}
	public String getAbr() {
		return abr.get();
	}
	public String getShtrp() {
		return shtrp.get();
	}
	public String getAats() {
		return aats.get();
	}
}

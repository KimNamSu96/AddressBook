package work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressLogic {
	Map<Character, List<Person>> mapList;
	
	public AddressLogic() {
		this.mapList= loadAddress();
	}

	public void getMainMenu() {
		System.out.println("=================주소록 메뉴=================");
		System.out.println("1.입력 2.출력 3.수정 4.삭제 5.검색 8.저장 9.종료");
		System.out.println("==========================================");
	}
	
	public int getNum(String txt) {//숫자를 입력받기 위한 메소드
		System.out.println(txt+"를 입력해주세요(숫자만)");
		Scanner sc = new Scanner(System.in);
		while(true) {
			try {
			int num = Integer.parseInt(sc.nextLine().trim());
			return num;
			}catch(NumberFormatException e) {
				System.out.println(String.format("%s는 숫자만 입력가능합니다.%n숫자를 입력해주세요", txt));
			}
		}
	}
	
	public String getName(String text) {//이름 유효성 검사
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println(text+" 이름을 입력하세요?");
			String name = sc.nextLine().trim();
			boolean isName = Pattern.matches("^[가-힣]{2,}$", name);
			if(isName) return name;
			else System.out.println("이름을 확인해주세요");
		}
	}
	
	public String getAddr(String text) {//주소 유효성 검사
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println(text+" 주소를 입력하세요?");
			String addr = sc.nextLine().trim();
			if(Pattern.matches("^[0-9가-힣]+", addr)) return addr;
			else System.out.println("주소는 한글과 숫자만 입력가능합니다");
		}
	}
	
	public String getTel(String text) {
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println(text+" 연락처를 입력하세요?(숫자만 입력해주세요.)");
			String tel = sc.nextLine().trim();
			if(Pattern.matches("^[0-9]{11}", tel)) return tel;
			else System.out.println("연락처는 11자리 숫자만 입력해주세요");
		}
	}
	
	public void seperateMainMenu() {
		while(true) {
			getMainMenu();
			int mainNum = getNum("메뉴번호");
			switch(mainNum) {
				case 1:
					insertPerson();
					break;
				case 2:
					printAddress();
					break;
				case 3:
					editPerson();
					break;
				case 4:
					deletePerson();
					break;
				case 5:
					searchPerson();
					break;
				case 8:
					saveAddress();
					break;
				case 9:
					System.out.println("종료합니다.");
					System.exit(0);
				default:
					System.out.println("올바른 메뉴번호를 입력해주세요");
			}
			
		}
	}//seperateMainMenu()
	
	//입력 메서드
	public void insertPerson() {
		Person person = new Person(getName("입력할"),getNum("입력할 나이"),getAddr("입력할"),getTel("입력할")); // 입력받은값으로 Person객체 생성
		char init = getInitialConsonant(person.getName()); // 이름의 초성값 가져오기
		if(mapList.containsKey(init))
			mapList.get(init).add(person); // map(key)로 얻어온 리스트에 person객체 추가
		else {
			List<Person> personList= new ArrayList<>();
			personList.add(person);
			mapList.put(init, personList);
		}
		System.out.println("[저장완료]:"+person);
		mapList= sortList();
	}
	
	//초성얻기 메소드
	public static char getInitialConsonant(String value) {
		char lastName = value.charAt(0);
		int index = (lastName - '가')/28/21; //초성의 인덱스 얻기
		char[] initialConsonants = {'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
		return initialConsonants[index];
	}
	
	//출력 메서드!!
	public void printAddress() {
		Set<Character> keys= mapList.keySet();
		for(Character key:keys) {
			System.out.println("["+key+"로 시작하는 이름]");
			List<Person> personList= mapList.get(key);
			personList.forEach(person-> System.out.println(person));
		}
	}
	
	//실행시 파일 불러오기 메서드!!
	public Map<Character, List<Person>> loadAddress(){
		Map<Character, List<Person>> mapList = new HashMap<Character, List<Person>>();
		BufferedReader br= null;
		Pattern pattern= Pattern.compile("이름:(?<name>[^ ]+) 나이:(?<age>[^ ]+) 주소:(?<address>[^ ]+) 연락처:(?<tel>[^ ]+)");
		try {
			br= new BufferedReader(new FileReader("src/work/address.txt"));
			String line;
			while((line=br.readLine())!=null) {
				Matcher matcher= pattern.matcher(line);
				if(matcher.find()) {
					Person person= new Person(matcher.group("name"), Integer.parseInt(matcher.group("age")),
												matcher.group("address"), matcher.group("tel"));
					char init= getInitialConsonant(person.getName());
					if(mapList.containsKey(init))
						mapList.get(init).add(person);
					else {
						List<Person> personList= new ArrayList<>();
						personList.add(person);
						mapList.put(init, personList);
					}
				}
			}
		}catch(FileNotFoundException e) {
			System.out.println("[저장된 주소록 없음]");
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mapList;
	}
	
	//정렬 메서드!!
	public Map<Character, List<Person>> sortList(){
		Set<Character> keys= mapList.keySet();
		Map<Character, List<Person>> map= new HashMap();
		for(Character key:keys) {
			List<Person> personList= mapList.get(key);
			personList.sort((o1, o2)-> o1.getName().compareTo(o2.getName()));
			map.put(key, personList);
		}
		Map<Character, List<Person>> sortedMap= new TreeMap<>(map);
		return sortedMap;
	}
	
	public void editPerson(){
		Person person= searchPerson();
		if(person != null) {
			char init= getInitialConsonant(person.getName());
			List<Person> personList= mapList.get(init);
			for(Person x:personList) {
				if(x.getName().equals(person.getName())) {
					x.setName(getName("변경할"));
					x.setAge(getNum("변경할 나이"));
					x.setAddr(getAddr("변경할"));
					x.setTel(getTel("변경할"));
					System.out.println("[수정완료]:"+x);
					mapList= sortList();
				}
			};
		}		
	}
	
	public Person searchPerson() {
		Person person= null;
		String name= getName("검색할");
		char init= getInitialConsonant(name);
		List<Person> personList= mapList.get(init);
		if(personList != null) 
			person= personList.stream()
					.filter(x-> x.getName().equals(name))
					.findFirst()
					.orElse(null);
		if(person != null) {
			System.out.println(person);
			return person;
		}
		else {
			System.out.println("검색된 정보가 없습니다");
			return null;
		}
	}
	
	public void deletePerson() {
		String name= getName("삭제할");
		char init= getInitialConsonant(name);
		List<Person> personList= mapList.get(init);
		if(personList != null) {
			for(int i=0; i<personList.size(); i++) {
				if(personList.get(i).getName().equals(name)) {
					Person person= personList.get(i);
					personList.remove(i);
					System.out.println("[삭제완료]:"+person);
					mapList= sortList();
				}
			}
		}
	}
	
	public void saveAddress() {
		FileOutputStream fos= null;
		try {
			fos= new FileOutputStream(new File("src/work/address.txt"));
			Set<Character> keys= mapList.keySet();
			for(Character key:keys) {
				List<Person> personList= mapList.get(key);
				for(Person person:personList) {
					fos.write(person.toString().getBytes());
					fos.write("\r\n".getBytes());
				}
			}
			System.out.println("[저장완료]");
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}finally {
			try {
				fos.close();
			}catch(IOException e) {
				e.printStackTrace();}
		}
		
	}
	
}//class

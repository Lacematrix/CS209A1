import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower
 * version). This is just a demo, and you can extend and implement functions based on this demo, or
 * implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                        Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]),
                        Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                        Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Integer> getPtcpCountByInst() {
        Stream<Course> courseStream = courses.stream();
        Map<String, Integer> Q1 = courseStream.collect(Collectors.groupingBy(Course::getInstitution,
                Collectors.summingInt(Course::getParticipants)));
        return new TreeMap<>(Q1);
    }

    //2
    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Stream<Course> courseStream = courses.stream();
        Map<String, Integer> Q2 = courseStream.collect(
                Collectors.groupingBy(Course::getICS, Collectors.summingInt(Course::getParticipants)));
        Map<String, Integer> Q2new = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                Q2.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Integer> mapping : list) {
            Q2new.put(mapping.getKey(), mapping.getValue());
        }
        return Q2new;
    }

    //3
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Stream<Course> courseStream = courses.stream();
        Map<String, List<List<String>>> Q3 = new TreeMap<>();
        Map<String, List<Course>> temp;
        List<String> teacher = new ArrayList<>();
        temp = courseStream.collect(Collectors.groupingBy(Course::getInstructors, Collectors.toList()));
        temp.forEach((s, courses1) -> {
                    String[] k;
                    k = s.split(", ");
                    Arrays.stream(k).forEach(s1 -> {
                        if (!teacher.contains(s1)) {
                            List<List<String>> list = new ArrayList<>();
                            List<String> t0 = new ArrayList<>();
                            List<String> t1 = new ArrayList<>();
                            list.add(t0);
                            list.add(t1);
                            Q3.put(s1, list);
                            teacher.add(s1);
                        }
                    });
                }
        );
        temp.forEach(((s, courses1) -> {
            String[] k = s.split(", ");
            if (k.length == 1) {
                List<List<String>> co = Q3.get(k[0]);
                courses1.forEach(course -> {
                    if (!co.get(0).contains(course.getTitle())) {
                        co.get(0).add(course.getTitle());
                    }
                });
                Q3.put(k[0], co);
            } else {
                for (int i = 0; i < k.length; i++) {
                    List<List<String>> co1 = Q3.get(k[i]);
                    courses1.forEach(course -> {
                        if (!co1.get(1).contains(course.getTitle())) {
                            co1.get(1).add(course.getTitle());
                        }
                    });
                    Q3.put(k[i], co1);
                }
            }
        }));
        Q3.forEach((s, lists) -> {

            for (int i = 0; i < lists.size(); i++) {
                lists.get(i).sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
            }
        });
        return Q3;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        Stream<Course> courseStream = courses.stream();
        if (by.equals("hours")) {
            Map<String, List<Course>> courseEach = courseStream.collect(
                    Collectors.groupingBy(Course::getTitle,
                            Collectors.toList()));
            Map<String, Double> Q4_1 = new TreeMap<>();
            courseEach = new TreeMap<>(courseEach);
            courseEach.forEach((s, courses1) -> {
                courses1.sort(Comparator.comparing(Course::getTotalHours).reversed());
                Q4_1.put(s, courses1.get(0).getTotalHours());
            });
            List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
                    Q4_1.entrySet());
            List<String> Q4_1L = new ArrayList<>();
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            for (int i = 0; i < topK; i++) {
                Q4_1L.add(list.get(i).getKey());
            }
            return Q4_1L;
        } else {
            Map<String, List<Course>> courseEach = courseStream.collect(
                    Collectors.groupingBy(Course::getTitle,
                            Collectors.toList()));
            Map<String, Integer> Q4_1 = new TreeMap<>();
            courseEach = new TreeMap<>(courseEach);
            courseEach.forEach((s, courses1) -> {
                courses1.sort(Comparator.comparing(Course::getParticipants).reversed());
                Q4_1.put(s, courses1.get(0).getParticipants());
            });
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                    Q4_1.entrySet());
            List<String> Q4_1L = new ArrayList<>();
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            for (int i = 0; i < topK; i++) {
                Q4_1L.add(list.get(i).getKey());
            }
            return Q4_1L;
        }
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited,
                                      double totalCourseHours) {
        Stream<Course> courseStream = courses.stream();
        Map<String, List<Course>> courseTemp = courseStream.filter(
                        course -> course.getPercentAudited() >= percentAudited)
                .filter(course -> course.totalHours <= totalCourseHours)
                .collect(Collectors.groupingBy(Course::getSubject, Collectors.toList()));
        List<String> course = new ArrayList<>();
        courseTemp.forEach((s, courses1) -> {
            String[] k = s.split(", ");
            if (k.length > 1) {
                k[k.length - 1] = k[k.length - 1].replace("and ", "");
            }
            for (int i = 0; i < k.length; i++) {
                String string = k[i].toLowerCase();
                k[i] = string;
            }
            boolean flag = false;
            for (int i = 0; i < k.length; i++) {
                if (k[i].contains(courseSubject.toLowerCase())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                courses1.forEach(course1 -> {
                    if (!course.contains(course1.getTitle())) {
                        course.add(course1.getTitle());
                    }
                });
            }
        });
        Collections.sort(course);
        return course;
    }

    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        Stream<Course> courseStream = courses.stream();
        Map<String, List<Course>> course = courseStream.collect(
                Collectors.groupingBy(Course::getNumber, Collectors.toList()));
        Map<String, Double> Q6_av = new HashMap<>();
        List<String> Q6 = new ArrayList<>();
        course.forEach((s, courses1) -> {
            double avgMA = 0;
            double avgM = 0;
            double BDH = 0;
            for (Course c : courses1) {
                avgMA += c.medianAge;
                avgM += c.percentMale;
                BDH += c.percentDegree;
            }
            avgMA /= courses1.size();
            avgM /= courses1.size();
            BDH /= courses1.size();
            double value = (age - avgMA) * (age - avgMA) +
                    (gender * 100 - avgM) * (gender * 100 - avgM) +
                    (isBachelorOrHigher * 100 - BDH) * (isBachelorOrHigher * 100 - BDH);
            courses1.sort(new Comparator<Course>() {
                @Override
                public int compare(Course o1, Course o2) {
                    return o2.launchDate.compareTo(o1.launchDate);
                }
            });
            Q6_av.put(courses1.get(0).title, value);
        });
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(
                Q6_av.entrySet());
        Collections.sort(list, Entry.comparingByValue());
        for (int i = 0; i < 10; i++) {
            Q6.add(list.get(i).getKey());
        }
        return Q6;
    }

}

class Course {

    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }

    public int getParticipants() {
        return participants;
    }

    public String getICS() {
        return String.format("%s-%s", institution, subject);
    }

    public String getInstructors() {
        return instructors;
    }

    public String getTitle() {
        return title;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public String getSubject() {
        return subject;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public String getNumber() {
        return number;
    }


}
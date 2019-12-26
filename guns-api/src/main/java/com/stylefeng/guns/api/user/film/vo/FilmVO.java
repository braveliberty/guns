package com.stylefeng.guns.api.user.film.vo;




import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class FilmVO implements Serializable {

    private int filmNum;
    private List<FilmInfo> FilmInfos;
    private int totalPage;
    private int nowPage;

    public int getFilmNum() {
        return filmNum;
    }

    public void setFilmNum(int filmNum) {
        this.filmNum = filmNum;
    }

    public List<FilmInfo> getFilmInfos() {
        return FilmInfos;
    }

    public void setFilmInfos(List<FilmInfo> filmInfos) {
        FilmInfos = filmInfos;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }
}

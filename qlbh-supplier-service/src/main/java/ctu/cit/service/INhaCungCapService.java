package ctu.cit.service;

import java.util.List;

import ctu.cit.model.NhaCungCap;

public interface INhaCungCapService {
    boolean them(NhaCungCap ncc);
    List<NhaCungCap> getAll();
    NhaCungCap tim(String ma);
    boolean capNhat(String ma, NhaCungCap ncc);
    boolean xoa(String ma);
}

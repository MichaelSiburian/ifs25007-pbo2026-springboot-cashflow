package org.delcom.ifs25007pbo2026springbootcashflow.domain.exception;
/**
 * Exception yang dilempar saat terjadi kegagalan validasi aturan bisnis atau input domain.
 */
public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }
}

class BNImo(nama : String, saldo: Long, noRekening: String): MobileBanking(nama, saldo, noRekening) {
    override fun transfer(dp: DigitalPayment, nominal: Long) {
        if(dp is BRImo) {
            checkFree = true
            println("Transfer berhasil ${printBuktiTransfer(penerima = dp, nominal = nominal )}")
        }
    }
}
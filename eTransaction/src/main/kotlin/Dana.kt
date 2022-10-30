class Dana(nama: String, saldo: Long, noHp: String): MobileWallet(nama, saldo, noHp) {
    val danaFreeTransferBank: Int = 1000;


    override fun transfer(dp: DigitalPayment, nominal: Long) {
        feeTransferBank = 1000
        if(dp is Ovo){
            print("DANA tidak valid ")
        }else{
            println("Transfer berhasil ${printBuktiTransfer(penerima = dp, nominal = nominal )}")
        }
    }
}
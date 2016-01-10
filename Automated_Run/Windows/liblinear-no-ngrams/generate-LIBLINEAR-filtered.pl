#!/usr/bin/perl
#
#  Author: Preslav Nakov
#  
#  Description: Generates LIBLINEAR from the GATE dump
#
#  Last modified: April 14, 2014
#
#

use warnings;
use strict;
use utf8;

use constant TRAIN_FILE    => 'train+dev2013-B-GATE.txt';
use constant TEST2013_FILE => 'test2013-B-Twitter-GATE.txt';
use constant TEST2014_FILE => 'test2014-GATE.txt';

my $filters_file = $ARGV[0] or die "No filters file is specified\n";
my %filters = ();
my %features = ();

print STDERR "Reading file $filters_file \n";
### 0. Build a list of filters
open FITLER, $filters_file or die;
#binmode (FITLER,  ":utf8");
while(my $line = <FITLER>) {
	
	## nrcSen	sent140	
	foreach my $filterValue (split /\t+/, $line) {
		##die "Wrong feature: '$featValue'" if ($featValue !~ /^(.+)==?([0-9\.\-E]+)$/);
		$filters{$filterValue}++;
	}
}
close FITLER or die;

foreach my $filt (sort keys %filters) {
	print STDERR "[$filt] \n" ;
}

### 1. Build a list of features
open TRAIN, TRAIN_FILE or die;
#binmode (TRAIN,  ":utf8");
while(<TRAIN>) {
	
	## 264183816548130816	15140428	positive	Gas by my house hit $3.39!!!! I’m going to Chapel Hill on Sat. :)	WC_1110010111111=1	nrcSen_uni_sent_sum=1.9209998	sen140_bi_ratio=0.8968879	sen140_uni_neg_sum=-884558	numElongWords=0	WC_110110=1	mpqa_neg_cnt=0	WC_2GRAM_10001110_0000=1	CD=2	NN_m=1	sen140_pair_pos_sum=103	CD_39=1	IN_on=1	WC_1111011111110=1	nrcSen_uni_neg_max=-195575	nrcSen_bi_neg_max=-6280	VBD=1	nrcSen_hash_pos_max=0	bl_pos_negated_cnt=0	mpqa_sent_last_token=0	WC_2GRAM_111010110100_1110010111111=1	PRP_i=1	bl_neg_cnt=0	CONT_SEQ_OF_QUESTION_MARKS=0	nrcEmo_neg_cnt=0	WC_1111110010011=1	bl_sent_last_token=0	VBD_hit=1	nrcSen_bi_pos_sum=6083	NN_house=1	TO_to=1	CONT_SEQ_OF_BOTH_EXCLAMATION_AND_QUESTION_MARKS=0	bl_sent_sum=0	WC_2GRAM_110110_1111011011111=1	nrcEmo_sent_sum=0	sen140_uni_pos_max=269073	msol_sent_last_token=0	nrcSen_uni_ratio=0.6721636	UH_)=1	CONT_SEQ_OF_EXCLAMATION_OR_QUESTION_MARKS=1	WC_11110101011011=1	$=1	LAST_TOKEN_ENDS_WITH_EXCLAMATION_OR_QUESTION_MARK=0	nrcSen_uni_pos_max=195575	nrcSen_hash_posneg_sum=0	nrcSen_pair_ratio=0.9433962	EndPosEmoticons=0	WC_011110111111011=1	TO=1	nrcEmo_sent_last_token=0	sen140_bi_pos_max=20855	NNP_hill=1	VBG_go=1	VBP_’=1	nrcSen_bi_pos_max=4422	nrcSen_pair_pos_sum=49	numHashTags=0	sen140_uni_pos_sum=867318	nrcSen_bi_posneg_sum=-2332	nrcSen_hash_sent_max=0.0	sen140_pair_sent_sum=4.214	sen140_bi_sent_max=0.454	.=5	IN_by=1	CONT_SEQ_OF_EXCLAMATION_MARKS=1	WC_2GRAM_1111111010110_1000100=1	msol_pos_cnt=7	NN=3	WC_011110001100=1	nrcSen_hash_pos_sum=0	WC_01010101000=1	PRP$=1	WC_111010110100=1	WC_111010111100=1	nrcSen_bi_sent_max=0.337	WC_1111111010110=1	IN=2	sen140_bi_sent_sum=-0.56	._!=3	mpqa_posneg_ratio=1.0	WC_2GRAM_01010101000_1111011111110=1	nrcSen_uni_posneg_sum=-303168	sen140_uni_sent_sum=2.0279999	nrcEmo_pos_cnt=0	numPosEmoticons=1	mpqa_neu_cnt=0	CD_3=1	msol_pos_negated_cnt=0	nrcSen_hash_neg_max=0	WC_2GRAM_011110001100_1010=1	stanford_sent_sum=5.16667	WC_2GRAM_10001110_10001110=1	nrcSen_uni_pos_sum=621585	sen140_bi_posneg_sum=-2528	WC_1110100=1	WC_2GRAM_1111110010011_1111011110100=1	WC_1010=1	PRP=1	sen140_uni_posneg_sum=-17240	WC_2GRAM_1000100_1110100=1	msol_posneg_ratio=2.0	mpqa_sent_sum=0	bl_posneg_ratio=1.0	VBP=1	sen140_bi_neg_max=-22464	WC_2GRAM_111111101101_10001110=1	mpqa_pos_cnt=0	nrcEmo_posneg_ratio=1.0	nrcSen_pair_neg_sum=52	numNegEmoticons=0	sen140_bi_pos_sum=21988	bl_pos_cnt=0	WC_101110110=1	nrcSen_hash_sent_sum=0.0	UH_:=1	sen140_pair_neg_sum=104	WC_2GRAM_11110101011011_101110110=1	UH_!=1	WC_2GRAM_10111110_011110111111011=1	WC_111111101101=1	NNP_chapel=1	WC_1111011011111=1	._.=2	sen140_bi_neg_sum=-24516	nrcSen_bi_neg_sum=-8415	sen140_pair_ratio=0.9904762	nrcSen_hash_neg_sum=0	WC_0000=1	nrcSen_pair_sent_sum=0.645	NNP_sat=1	sen140_uni_neg_max=-269073	UH=3	WC_10111110=1	VBG=1	WC_1000100=1	PRP$_my=1	NN_gas=1	msol_sent_sum=4	nrcSen_hash_ratio=1.0	nrcEmo_pos_negated_cnt=0	numAllNegationTokens=0	nrcSen_bi_sent_sum=0.914	EndNegEmoticons=0	numNegations=0	numAllCaps=0	msol_neg_cnt=-3	WC_1111011110100=1	mpqa_pos_negated_cnt=0	sen140_uni_ratio=0.98051006	WC_10001110=1	$_$=1	nrcSen_bi_ratio=0.72290874	nrcSen_uni_neg_sum=-924753	NNP=3	
	die "Wrong file format!" if (!/^[^\t]+\t[^\t]+\t(positive|negative|neutral)\t[^\t]+\t(.+)$/);
	print $label
	print $rest
	my ($label, $rest) = ($1, $2);

	foreach my $featValue (split /\t+/, $rest) {
		die "Wrong feature: featValue" if ($featValue !~ /^(.+)==?([0-9\.\-E]+)$/);
		my ($feat, $value) = ($1, $2);
		$features{$feat}++;
	}
}
close TRAIN or die;

### 2. Remove features starting with filter
 foreach my $filt1 (sort keys %filters) {
	 print STDERR "Excluding features starting with [$filt1]:\n" ;

	 foreach my $featKey (sort keys %features) {
		 if($featKey =~ /^$filt1/){	
			 delete $features{$featKey};
			 print STDERR "- $featKey \n";
		 }
	 }
 }

### 3. Sort the features to produce an index
my $index = 1;
foreach my $feat (sort keys %features) {
	$features{$feat} = $index;
	$index++;
}


### 3. Write the features files
&writeFeaturesFile(TRAIN_FILE);
&writeFeaturesFile(TEST2013_FILE);
&writeFeaturesFile(TEST2014_FILE);


############
### SUBS ###
############

sub writeFeaturesFile() {
	my $fname = shift;

	print STDERR "Processing $fname...\n";
	
	open INPUT, $fname or die;
#	binmode (INPUT,  ":utf8");
	
	open OUTPUT, ">$fname\.liblinear" or die;
#	binmode (OUTPUT,  ":utf8");

	while (<INPUT>) {	
		## 264183816548130816	15140428	positive	Gas by my house hit $3.39!!!! I’m going to Chapel Hill on Sat. :)	WC_1110010111111=1	nrcSen_uni_sent_sum=1.9209998	sen140_bi_ratio=0.8968879	sen140_uni_neg_sum=-884558	numElongWords=0	WC_110110=1	mpqa_neg_cnt=0	WC_2GRAM_10001110_0000=1	CD=2	NN_m=1	sen140_pair_pos_sum=103	CD_39=1	IN_on=1	WC_1111011111110=1	nrcSen_uni_neg_max=-195575	nrcSen_bi_neg_max=-6280	VBD=1	nrcSen_hash_pos_max=0	bl_pos_negated_cnt=0	mpqa_sent_last_token=0	WC_2GRAM_111010110100_1110010111111=1	PRP_i=1	bl_neg_cnt=0	CONT_SEQ_OF_QUESTION_MARKS=0	nrcEmo_neg_cnt=0	WC_1111110010011=1	bl_sent_last_token=0	VBD_hit=1	nrcSen_bi_pos_sum=6083	NN_house=1	TO_to=1	CONT_SEQ_OF_BOTH_EXCLAMATION_AND_QUESTION_MARKS=0	bl_sent_sum=0	WC_2GRAM_110110_1111011011111=1	nrcEmo_sent_sum=0	sen140_uni_pos_max=269073	msol_sent_last_token=0	nrcSen_uni_ratio=0.6721636	UH_)=1	CONT_SEQ_OF_EXCLAMATION_OR_QUESTION_MARKS=1	WC_11110101011011=1	$=1	LAST_TOKEN_ENDS_WITH_EXCLAMATION_OR_QUESTION_MARK=0	nrcSen_uni_pos_max=195575	nrcSen_hash_posneg_sum=0	nrcSen_pair_ratio=0.9433962	EndPosEmoticons=0	WC_011110111111011=1	TO=1	nrcEmo_sent_last_token=0	sen140_bi_pos_max=20855	NNP_hill=1	VBG_go=1	VBP_’=1	nrcSen_bi_pos_max=4422	nrcSen_pair_pos_sum=49	numHashTags=0	sen140_uni_pos_sum=867318	nrcSen_bi_posneg_sum=-2332	nrcSen_hash_sent_max=0.0	sen140_pair_sent_sum=4.214	sen140_bi_sent_max=0.454	.=5	IN_by=1	CONT_SEQ_OF_EXCLAMATION_MARKS=1	WC_2GRAM_1111111010110_1000100=1	msol_pos_cnt=7	NN=3	WC_011110001100=1	nrcSen_hash_pos_sum=0	WC_01010101000=1	PRP$=1	WC_111010110100=1	WC_111010111100=1	nrcSen_bi_sent_max=0.337	WC_1111111010110=1	IN=2	sen140_bi_sent_sum=-0.56	._!=3	mpqa_posneg_ratio=1.0	WC_2GRAM_01010101000_1111011111110=1	nrcSen_uni_posneg_sum=-303168	sen140_uni_sent_sum=2.0279999	nrcEmo_pos_cnt=0	numPosEmoticons=1	mpqa_neu_cnt=0	CD_3=1	msol_pos_negated_cnt=0	nrcSen_hash_neg_max=0	WC_2GRAM_011110001100_1010=1	stanford_sent_sum=5.16667	WC_2GRAM_10001110_10001110=1	nrcSen_uni_pos_sum=621585	sen140_bi_posneg_sum=-2528	WC_1110100=1	WC_2GRAM_1111110010011_1111011110100=1	WC_1010=1	PRP=1	sen140_uni_posneg_sum=-17240	WC_2GRAM_1000100_1110100=1	msol_posneg_ratio=2.0	mpqa_sent_sum=0	bl_posneg_ratio=1.0	VBP=1	sen140_bi_neg_max=-22464	WC_2GRAM_111111101101_10001110=1	mpqa_pos_cnt=0	nrcEmo_posneg_ratio=1.0	nrcSen_pair_neg_sum=52	numNegEmoticons=0	sen140_bi_pos_sum=21988	bl_pos_cnt=0	WC_101110110=1	nrcSen_hash_sent_sum=0.0	UH_:=1	sen140_pair_neg_sum=104	WC_2GRAM_11110101011011_101110110=1	UH_!=1	WC_2GRAM_10111110_011110111111011=1	WC_111111101101=1	NNP_chapel=1	WC_1111011011111=1	._.=2	sen140_bi_neg_sum=-24516	nrcSen_bi_neg_sum=-8415	sen140_pair_ratio=0.9904762	nrcSen_hash_neg_sum=0	WC_0000=1	nrcSen_pair_sent_sum=0.645	NNP_sat=1	sen140_uni_neg_max=-269073	UH=3	WC_10111110=1	VBG=1	WC_1000100=1	PRP$_my=1	NN_gas=1	msol_sent_sum=4	nrcSen_hash_ratio=1.0	nrcEmo_pos_negated_cnt=0	numAllNegationTokens=0	nrcSen_bi_sent_sum=0.914	EndNegEmoticons=0	numNegations=0	numAllCaps=0	msol_neg_cnt=-3	WC_1111011110100=1	mpqa_pos_negated_cnt=0	sen140_uni_ratio=0.98051006	WC_10001110=1	$_$=1	nrcSen_bi_ratio=0.72290874	nrcSen_uni_neg_sum=-924753	NNP=3	
		die "Wrong file format!" if (!/^[^\t]+\t[^\t]+\t(positive|negative|neutral|unknwn)\t[^\t]+\t(.+)$/);
		my ($label, $rest) = ($1, $2);

		if ($label =~ /positive/) {
			print OUTPUT "1";
		}
		elsif ($label =~ /negative/) {
			print OUTPUT "2";
		}
		elsif ($label =~ /neutral/) {
			print OUTPUT "3";
		}
		elsif ($label =~ /unknwn/) {
			print OUTPUT "4";
		}
		else { die "label=$label"; }

		my %curFeats = ();
		foreach my $featValue (split /\t+/, $rest) {
			die "Wrong feature: featValue" if ($featValue !~ /^(.+)==?([0-9\.\-E]+)$/);
			my ($feat, $value) = ($1, $2);
			$curFeats{$feat} = $value;
		}

		foreach my $ft (sort keys %curFeats) {
			print OUTPUT " $features{$ft}:$curFeats{$ft}" if (defined $features{$ft});
		}

		print OUTPUT "\n";

	}

	close INPUT or die;
	close OUTPUT or die;
}
